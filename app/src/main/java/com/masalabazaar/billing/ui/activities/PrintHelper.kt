package com.masalabazaar.billing.ui.activities

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import java.io.File

class PrintHelper(private val context: Context) {

    fun printPDF(pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfPrintAdapter(pdfFile)
        val printJob: PrintJob = printManager.print("Masala Billing Invoice", printAdapter, PrintAttributes.Builder().build())
    }
}
