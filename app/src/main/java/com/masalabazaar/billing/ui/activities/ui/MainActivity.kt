package com.masalabazaar.billing.ui.activities.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.adapter.BillAdapter
import com.masalabazaar.billing.ui.activities.data.BillItem
import com.masalabazaar.billing.ui.activities.database.DatabaseHelper
import com.masalabazaar.billing.ui.activities.pdf.PDFGenerator
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillAdapter
    private lateinit var totalAmountText: TextView
    private lateinit var paidAmountInput: EditText
    private lateinit var remainingAmountText: TextView
    private lateinit var generatePdfButton: Button
    private lateinit var historyButton: Button
    private lateinit var addItemButton: Button
    private lateinit var customerNameInput: EditText
    private val items = mutableListOf<BillItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "आदर्श ऍग्रो इंडस्ट्रीज - एडवण"
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.getOverflowIcon()?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        totalAmountText = findViewById(R.id.totalAmount)
        paidAmountInput = findViewById(R.id.paidAmount)
        remainingAmountText = findViewById(R.id.remainingAmount)
        generatePdfButton = findViewById(R.id.generatePdfButton)
        historyButton = findViewById(R.id.historyButton)
        addItemButton = findViewById(R.id.addItemButton)
        customerNameInput = findViewById(R.id.customerNameInput)

        setupRecyclerView()
        loadItems()
        setupButtonActions()
        setupPaidAmountListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.menu_add_items -> {
                startActivity(Intent(this, ItemEntryActivity::class.java))
                true
            }
            R.id.menu_clear -> {
                clearFields()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateRemainingAmount() {
        val totalAmount = totalAmountText.text.toString().replace("₹ ", "").toDoubleOrNull() ?: 0.0
        val paidAmount = paidAmountInput.text.toString().replace("₹ ", "").toDoubleOrNull() ?: 0.0
        val remainingAmount = totalAmount - paidAmount
        remainingAmountText.text = "₹ $remainingAmount"
    }

    private fun setupPaidAmountListener() {
        paidAmountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateRemainingAmount()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun clearFields() {
        customerNameInput.text.clear()
        paidAmountInput.text.clear()

        for (item in items) {
            item.quantity = 0.0
        }
        adapter.notifyDataSetChanged()
        updateTotalAmount()
        Toast.makeText(this, "Cleared !!!", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BillAdapter(items) { updateTotalAmount() }
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setupButtonActions() {
        generatePdfButton.setOnClickListener {
            val userName = customerNameInput.text.toString().trim().replace(" ", "_")
            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter the Customer Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            generateAndSavePDF(userName)
        }

        historyButton.visibility = View.GONE
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        addItemButton.visibility = View.GONE
        addItemButton.setOnClickListener {
            startActivity(Intent(this, ItemEntryActivity::class.java))
        }
    }

    private fun loadItems() {
        val dbHelper = DatabaseHelper(this)
        val itemList = dbHelper.getItems()

        items.clear()
        if (itemList.isNotEmpty()) {
            items.addAll(itemList)
        } else {
            for (i in 1..30) {
                items.add(BillItem("Item $i", 0.0, 0.0))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateTotalAmount() {
        val total = items.sumOf { it.ratePerKg * it.quantity }
        totalAmountText.text = "₹ $total"
        updateRemainingAmount()
    }

    private fun generateAndSavePDF(userName: String) {
        val pdfGenerator = PDFGenerator(this)
        val pdfFile: File? = pdfGenerator.generatePDF(items, totalAmountText.text.toString(), "₹ "+paidAmountInput.text.toString(), remainingAmountText.text.toString(), userName)

        pdfFile?.let {
            val dbHelper = DatabaseHelper(this)
            dbHelper.saveReport(
                filename = pdfFile.name,
                customer = userName,
                amount = totalAmountText.text.toString()
            )
            Toast.makeText(this, "PDF Generated Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
