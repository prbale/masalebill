package com.masalabazaar.billing.ui.activities.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var generatePdfButton: Button
    private lateinit var historyButton: Button
    private lateinit var addItemButton: Button
    private lateinit var customerNameInput: EditText
    private val items = mutableListOf<BillItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalAmountText = findViewById(R.id.totalAmount)
        generatePdfButton = findViewById(R.id.generatePdfButton)
        historyButton = findViewById(R.id.historyButton)
        addItemButton = findViewById(R.id.addItemButton)
        customerNameInput = findViewById(R.id.customerNameInput)

        setupRecyclerView()
        loadItems()
        setupButtonActions()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BillAdapter(items) { updateTotalAmount() }
        recyclerView.adapter = adapter
    }

    private fun setupButtonActions() {
        generatePdfButton.setOnClickListener {
            val userName = customerNameInput.text.toString().trim()
            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter the Customer Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            generateAndSavePDF(userName)
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

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
        totalAmountText.text = "Total: ₹$total"
    }

    private fun generateAndSavePDF(userName: String) {
        val pdfGenerator = PDFGenerator(this)
        val pdfFile: File? = pdfGenerator.generatePDF(items, totalAmountText.text.toString(), userName)

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
