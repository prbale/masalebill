package com.masalabazaar.billing.ui.activities

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.masalabazaar.billing.ui.activities.BillItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFGenerator(private val context: Context) {

    fun generatePDF(items: List<BillItem>, totalAmount: String, customerName: String): File? {
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

        // Draw Customer Name
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Customer: $customerName", 20f, 50f, paint)

        // Draw Table Header with Background Color
        val headerPaint = Paint()
        headerPaint.color = Color.LTGRAY
        canvas.drawRect(20f, 70f, 380f, 90f, headerPaint)

        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Item Name", 25f, 85f, paint)
        canvas.drawText("Rate (₹/kg)", 150f, 85f, paint)
        canvas.drawText("Qty (kg)", 250f, 85f, paint)
        canvas.drawText("Total (₹)", 320f, 85f, paint)

        paint.typeface = Typeface.DEFAULT
        var yPosition = 100f

        // Draw Table Data
        for (item in items) {
            val totalPrice = item.ratePerKg * item.quantity
            canvas.drawText(item.name, 25f, yPosition, paint)
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
        paint.color = Color.RED
        canvas.drawText("Total Amount: $totalAmount", 250f, yPosition, paint)

        document.finishPage(page)

        val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${customerName}_bill.pdf")
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
