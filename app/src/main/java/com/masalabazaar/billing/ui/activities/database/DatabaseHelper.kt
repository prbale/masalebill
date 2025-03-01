package com.masalabazaar.billing.ui.activities.database
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.masalabazaar.billing.ui.activities.data.BillItem
import com.masalabazaar.billing.ui.activities.data.ReportItem

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MasalaBilling.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE report (id INTEGER PRIMARY KEY AUTOINCREMENT, filename TEXT, customer TEXT, amount TEXT, dateTime TEXT)")
            db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ratePerKg REAL)")
            seedDatabase(db)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating tables", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS report")
            db.execSQL("DROP TABLE IF EXISTS items")
            onCreate(db)
        } catch (e: Exception) {
            Log.e(TAG, "Error upgrading database", e)
        }
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


    private fun seedDatabase(db: SQLiteDatabase) {
        val items = listOf(
            Pair("Turmeric", 200.0),
            Pair("Red Chilli", 300.0),
            Pair("Coriander", 250.0)
        )
        for (item in items) {
            val values = ContentValues().apply {
                put("name", item.first)
                put("ratePerKg", item.second)
            }
            db.insert("items", null, values)
        }
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
        try {
            val values = ContentValues().apply {
                put("filename", filename)
                put("customer", customer)
                put("amount", amount)
                put("dateTime", System.currentTimeMillis().toString())  // Store timestamp
            }
            val rowId = db.insert("report", null, values)
            if (rowId == -1L) {
                Log.e(TAG, "Error inserting report")
            } else {
                Log.d(TAG, "Report inserted successfully with rowId: $rowId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving report", e)
        } finally {
            db.close()
        }
    }

    fun getSavedReports(): List<ReportItem> {
        val db = readableDatabase
        val reports = mutableListOf<ReportItem>()
        val cursor = db.rawQuery("SELECT filename, customer, amount, dateTime FROM report", null)
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving reports", e)
        } finally {
            cursor.close()
            db.close()
        }
        return reports
    }
}