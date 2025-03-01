package com.masalabazaar.billing.ui.activities.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.adapter.HistoryAdapter
import com.masalabazaar.billing.ui.activities.data.ReportItem
import com.masalabazaar.billing.ui.activities.database.DatabaseHelper
import com.masalabazaar.billing.ui.activities.pdf.PrintHelper
import java.io.File

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var reports: List<ReportItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        loadHistory()
    }

    private fun loadHistory() {
        val dbHelper = DatabaseHelper(this)
        reports = dbHelper.getSavedReports()

        val adapter = HistoryAdapter(this, reports) { report ->
            showOptionsDialog(report)
        }
        historyRecyclerView.adapter = adapter
    }

    private fun showOptionsDialog(reportItem: ReportItem) {
        val options = arrayOf("View PDF", "Print")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewPDF(reportItem.customerName)
                1 -> printPDF(reportItem.customerName)
            }
        }
        builder.show()
    }

    private fun viewPDF(customerName: String) {
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("pdf_path", pdfFile.absolutePath)
        startActivity(intent)
    }

    private fun printPDF(customerName: String) {
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")
        val printHelper = PrintHelper(this)
        printHelper.printPDF(pdfFile)
    }
}
