package com.masalabazaar.billing.ui.activities.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.adapter.BillAdapter
import com.masalabazaar.billing.ui.activities.data.BillItem
import com.masalabazaar.billing.ui.activities.pdf.PDFGenerator
import com.masalabazaar.billing.ui.activities.pdf.PrintHelper
import com.masalabazaar.billing.ui.activities.database.DatabaseHelper
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillAdapter
    private lateinit var totalAmountText: TextView
    private lateinit var generatePdfButton: Button
    private lateinit var historyButton: Button
    private lateinit var itemDataFeedButton: Button
    private val items = mutableListOf<BillItem>()

    private lateinit var itemEntryLauncher: ActivityResultLauncher<Intent>

    private lateinit var customerNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalAmountText = findViewById(R.id.totalAmount)

        customerNameInput = findViewById(R.id.customerNameInput)

        generatePdfButton = findViewById(R.id.generatePdfButton)
        historyButton = findViewById(R.id.historyButton)
        itemDataFeedButton = findViewById(R.id.itemDataFeedButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BillAdapter(items) { updateTotalAmount() }
        recyclerView.adapter = adapter

        loadItems()

        generatePdfButton.setOnClickListener {

            val customerName = customerNameInput.text.toString().trim()
            if (customerName.isEmpty()) {
                Toast.makeText(this, "Please enter the Customer Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pdfGenerator = PDFGenerator(this)
            val file: File? = pdfGenerator.generatePDF(items, totalAmountText.text.toString(), customerName)
            file?.let { it1 ->
                PrintHelper(this).printPDF(it1)
            }

            dbHelper.saveReport(
                filename = file?.name!!,
                customer = customerName.replace(" ", "_"),
                amount = totalAmountText.text.toString()
                )
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Initialize Activity Result Launcher
        itemEntryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
            }
            else {
                loadItems()
            }
        }

        itemDataFeedButton.setOnClickListener {
            val intent = Intent(this, ItemEntryActivity::class.java)
            itemEntryLauncher.launch(intent)
        }
    }

    val dbHelper = DatabaseHelper(this)

    private fun loadItems() {
        val dbHelper = DatabaseHelper(this)
        val itemList = dbHelper.getItems()

        if (itemList.isNotEmpty()) {
            items.clear()
            items.addAll(itemList)
        } else {
            // Initialize with default 0 values if no data is in the database
            items.clear()
            for (i in 1..30) {
                items.add(BillItem("Item $i", 0.0, 0.0))
            }
        }
        adapter.notifyDataSetChanged()

        runOnUiThread {
            adapter.notifyItemRangeChanged(0, items.size)
        }
    }

    private fun updateTotalAmount() {
        val total = items.sumOf { it.ratePerKg * it.quantity }
        totalAmountText.text = "Total Amount : $total"
    }
}
