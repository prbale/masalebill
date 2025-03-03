package com.masalabazaar.billing.ui.activities.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.pdf.PrintHelper
import java.io.File

/**
 * NOT USED
 */

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Report"
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.getOverflowIcon()?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar)

        webView = findViewById(R.id.pdfWebView)

        val pdfPath = intent.getStringExtra("pdf_path")
        if (pdfPath != null) {
            displayPDF(pdfPath)
        } else {
            finish() // Close activity if no file path is provided
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pdf_viewer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_print -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun printPDF(customerName: String) {
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")
        val printHelper = PrintHelper(this)
        printHelper.printPDF(pdfFile)
    }

    private fun displayPDF(pdfPath: String) {
        val file = File(pdfPath)
        if (file.exists()) {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
            webView.loadUrl("file:///$pdfPath")
        } else {
            finish() // Close activity if file is missing
        }
    }
}
