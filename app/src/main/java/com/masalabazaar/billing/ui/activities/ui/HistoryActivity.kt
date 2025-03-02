package com.masalabazaar.billing.ui.activities.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Report History"
        setSupportActionBar(toolbar)

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
        val options = arrayOf("View PDF", "Print", "Delete Report")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewPDF(reportItem.customerName.replace(" ", "_"))
                1 -> printPDF(reportItem.customerName.replace(" ", "_"))
                2 -> showDeleteConfirmation(reportItem)
            }
        }
        builder.show()
    }

    private fun viewPDF(customerName: String) {
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")

        if (pdfFile.exists()) {
            val uri = FileProvider.getUriForFile(this, "com.masalabazaar.billing.provider", pdfFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun printPDF(customerName: String) {
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")
        val printHelper = PrintHelper(this)
        printHelper.printPDF(pdfFile)
    }

    private fun showDeleteConfirmation(reportItem: ReportItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this report?")
        builder.setPositiveButton("Yes") { _, _ -> deleteReport(reportItem) }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun deleteReport(reportItem: ReportItem) {
        val dbHelper = DatabaseHelper(this)
        dbHelper.deleteReport(reportItem.fileName)

        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${reportItem.fileName}")
        if (pdfFile.exists()) {
            pdfFile.delete()
        }

        loadHistory() // Refresh list after deletion
    }
}
