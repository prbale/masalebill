package com.masalabazaar.billing.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.masalabazaar.billing.R
import java.io.File

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        webView = findViewById(R.id.pdfWebView)

        val pdfPath = intent.getStringExtra("pdf_path")
        if (pdfPath != null) {
            displayPDF(pdfPath)
        } else {
            finish() // Close activity if no file path is provided
        }
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
