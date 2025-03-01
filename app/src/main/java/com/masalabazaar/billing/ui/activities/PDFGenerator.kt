package com.masalabazaar.billing.ui.activities

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFGenerator(private val context: Context) {

    fun generatePDF(items: List<BillItem>, totalAmount: String): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        var yPosition = 40

        canvas.drawText("Masala Billing Invoice", 10f, yPosition.toFloat(), paint)
        yPosition += 30

        items.forEach {
            canvas.drawText("${it.name}: ₹${it.ratePerKg} x ${it.quantity}kg", 10f, yPosition.toFloat(), paint)
            yPosition += 20
        }
        canvas.drawText("Total: $totalAmount", 10f, yPosition.toFloat(), paint)
        document.finishPage(page)

        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bill.pdf")
        try {
            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            document.close()
            fos.close()
            return pdfFile
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
