package com.masalabazaar.billing.ui.activities.pdf

import android.content.Context
import android.os.Environment
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.masalabazaar.billing.ui.activities.data.BillItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PDFGenerator(private val context: Context) {

    fun generatePDF(
        items: List<BillItem>,
        totalAmount: String,
        paidAmount: String,
        remainingAmount: String,
        customerName: String): File? {

        try {

            val document = Document()
            val fileName = "${customerName.replace(" ", "_")}_bill.pdf"
            val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))

            document.open()

            // Marathi Font
            val baseFont = BaseFont.createFont("assets/marathi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            val titleFont = Font(baseFont, 18f, Font.BOLD)
            val headerFont = Font(baseFont, 16f, Font.BOLD)
            val textFont = Font(baseFont, 14f, Font.NORMAL)
            val textLargeFont = Font(baseFont, 17f, Font.NORMAL)

            // Title
            val title = Paragraph("आदर्श ऍग्रो इंडस्ट्रीज - एडवण", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            val producer = Paragraph(" उत्पादक - नामांकित लक्ष्मी मसाले ", textFont)
            producer.alignment = Element.ALIGN_CENTER
            document.add(producer)

            val owner = Paragraph(" श्री. अरविंद चौधरी - 97766672976 ", textFont)
            owner.alignment = Element.ALIGN_CENTER
            document.add(owner)

            val customerDetails = Paragraph("\nग्राहक: ${customerName.trim().replace("_", " ")}  |  दिनांक: ${getCurrentDate()}\n", textFont)
            document.add(customerDetails)

            document.add(Paragraph(" ", textFont))
            document.add(Paragraph(" ", textFont))

            // Create Table
            val table = PdfPTable(4)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(2f, 3f, 2f, 2f))

            addTableHeader(table, headerFont)
            addTableRows(table, items, textFont)

            document.add(table)

            // Add Total, Paid, and Remaining Amount
            val summaryTable = PdfPTable(2)
            summaryTable.widthPercentage = 100f
            summaryTable.setWidths(floatArrayOf(4f, 3f))
            document.add(Paragraph(" ", textFont))
            document.add(Paragraph(" ", textFont))
            addSummaryRow(summaryTable, "एकूण रक्कम", totalAmount, textFont)
            addSummaryRow(summaryTable, "जमा रक्कम", paidAmount, textFont)
            addSummaryRow(summaryTable, "बाकी रक्कम", remainingAmount, textFont)
            document.add(summaryTable)

            document.close()
            return pdfFile
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
        return null
    }

    private fun addSummaryRow(table: PdfPTable, label: String, value: String, font: Font) {
        val labelCell = PdfPCell(Phrase(label, font))
        labelCell.horizontalAlignment = Element.ALIGN_LEFT
        labelCell.border = PdfPCell.NO_BORDER
        table.addCell(labelCell)

        val valueCell = PdfPCell(Phrase(value, font))
        valueCell.horizontalAlignment = Element.ALIGN_RIGHT
        valueCell.border = PdfPCell.NO_BORDER
        table.addCell(valueCell)
    }

    private fun addTableHeader(table: PdfPTable, font: Font) {
        val headers = arrayOf("क्रमांक", "वस्तू", "प्रमाण (किलो)", "दर (प्रति किलो)")

        for (header in headers) {
            val cell = PdfPCell(Phrase(header, font))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = BaseColor.LIGHT_GRAY
            table.addCell(cell)
        }
    }

    private fun addTableRows(table: PdfPTable, items: List<BillItem>, font: Font) {
        var count = 1
        for (item in items) {
            table.addCell(Phrase("$count", font))
            table.addCell(Phrase(item.name, font))
            table.addCell(Phrase("${item.quantity}", font))
            table.addCell(Phrase("${item.ratePerKg}", font))
            count++
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}

