package com.masalabazaar.billing.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masalabazaar.billing.R
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillAdapter
    private lateinit var totalAmountText: TextView
    private lateinit var generatePdfButton: Button
    private lateinit var historyButton: Button
    private lateinit var itemDataFeedButton: Button
    private val items = mutableListOf<BillItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalAmountText = findViewById(R.id.totalAmount)
        generatePdfButton = findViewById(R.id.generatePdfButton)
        historyButton = findViewById(R.id.historyButton)
        itemDataFeedButton = findViewById(R.id.itemDataFeedButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BillAdapter(items) { updateTotalAmount() }
        recyclerView.adapter = adapter

        loadItems()

        generatePdfButton.setOnClickListener {
            val pdfGenerator = PDFGenerator(this)
            val file: File? = pdfGenerator.generatePDF(items, totalAmountText.text.toString(), "Prashant Bale")
            file?.let { it1 ->
                PrintHelper(this).printPDF(it1)
            }

            dbHelper.saveReport(
                filename = file?.name!!,
                customer = "Prashant Bale".replace(" ", "_"),
                amount = totalAmountText.text.toString()
                )
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        itemDataFeedButton.setOnClickListener {
            val intent = Intent(this, ItemEntryActivity::class.java)
            startActivity(intent)
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
    }

    private fun updateTotalAmount() {
        val total = items.sumOf { it.ratePerKg * it.quantity }
        totalAmountText.text = "$total"
    }
}
