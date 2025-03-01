package com.masalabazaar.billing.ui.activities.database


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.masalabazaar.billing.ui.activities.data.BillItem
import com.masalabazaar.billing.ui.activities.data.ReportItem

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ratePerKg REAL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS reports (id INTEGER PRIMARY KEY AUTOINCREMENT, filename TEXT, customer TEXT, amount TEXT, dateTime TEXT)")
        Log.d("DatabaseHelper", "Tables created: items, reports")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS items")
        db.execSQL("DROP TABLE IF EXISTS reports")
        onCreate(db)
    }

    fun saveItems(items: List<BillItem>) {
        val db = writableDatabase
        db.execSQL("DELETE FROM items") // Clear old items before saving new ones

        for (item in items) {
            val values = ContentValues().apply {
                put("name", item.name)
                put("ratePerKg", item.ratePerKg)
            }
            db.insert("items", null, values)
        }
        db.close()
    }

    fun getItems(): List<BillItem> {
        val items = mutableListOf<BillItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM items", null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val ratePerKg = cursor.getDouble(cursor.getColumnIndexOrThrow("ratePerKg"))
            items.add(BillItem(name, ratePerKg, 0.0))
        }
        cursor.close()
        return items
    }

    fun saveReport(filename: String, customer: String, amount: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("filename", filename)
            put("customer", customer)
            put("amount", amount)
            put("dateTime", System.currentTimeMillis().toString())
        }
        val result = db.insert("reports", null, values)
        if (result == -1L) {
            Log.e("DatabaseHelper", "Error inserting into reports table")
        } else {
            Log.d("DatabaseHelper", "Report saved successfully: $filename")
        }
        db.close()
    }

    fun getSavedReports(): List<ReportItem> {
        val db = readableDatabase
        val reports = mutableListOf<ReportItem>()
        try {
            val cursor = db.rawQuery("SELECT filename, customer, amount, dateTime FROM reports", null)
            while (cursor.moveToNext()) {
                reports.add(
                    ReportItem(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(2)
                    )
                )
            }
            cursor.close()
        } catch (e: android.database.sqlite.SQLiteException) {
            e.printStackTrace()
            Log.e("DatabaseHelper", "Error reading reports table: ${e.message}")
            onCreate(db) // Recreate the table if it doesn’t exist
        }
        return reports
    }

    fun deleteReport(filename: String) {
        val db = writableDatabase
        val result = db.delete("reports", "filename = ?", arrayOf(filename))
        if (result > 0) {
            Log.d("DatabaseHelper", "Report deleted successfully: $filename")
        } else {
            Log.e("DatabaseHelper", "Failed to delete report: $filename")
        }
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "MasalaBilling.db"
        private const val DATABASE_VERSION = 2
    }
}
