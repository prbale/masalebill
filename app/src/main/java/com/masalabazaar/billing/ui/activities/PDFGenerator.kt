package com.example.masalabilling

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.masalabazaar.billing.ui.activities.BillItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFGenerator(private val context: Context) {

    fun generatePDF(items: List<BillItem>, totalAmount: String): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT_BOLD

        // Draw Header
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Masala Billing Invoice", 200f, 30f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Draw Table Header
        var yPosition = 60f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Item Name", 20f, yPosition, paint)
        canvas.drawText("Rate (₹/kg)", 150f, yPosition, paint)
        canvas.drawText("Qty (kg)", 250f, yPosition, paint)
        canvas.drawText("Total (₹)", 320f, yPosition, paint)

        paint.typeface = Typeface.DEFAULT
        yPosition += 20f
        canvas.drawLine(20f, yPosition, 380f, yPosition, paint)
        yPosition += 20f

        // Draw Table Data
        for (item in items) {
            val totalPrice = item.ratePerKg * item.quantity
            canvas.drawText(item.name, 20f, yPosition, paint)
            canvas.drawText("₹${item.ratePerKg}", 150f, yPosition, paint)
            canvas.drawText("${item.quantity}", 250f, yPosition, paint)
            canvas.drawText("₹$totalPrice", 320f, yPosition, paint)
            yPosition += 20f
        }

        // Draw Footer
        yPosition += 10f
        canvas.drawLine(20f, yPosition, 380f, yPosition, paint)
        yPosition += 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Total Amount: $totalAmount", 250f, yPosition, paint)

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
