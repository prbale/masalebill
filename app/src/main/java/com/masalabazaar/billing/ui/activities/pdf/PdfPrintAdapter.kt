package com.masalabazaar.billing.ui.activities.pdf

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintAttributes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class PdfPrintAdapter(private val pdfFile: File) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val printDocumentInfo = PrintDocumentInfo.Builder(pdfFile.name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback?.onLayoutFinished(printDocumentInfo, true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        try {
            val inputStream = FileInputStream(pdfFile)
            val outputStream = FileOutputStream(destination?.fileDescriptor)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
