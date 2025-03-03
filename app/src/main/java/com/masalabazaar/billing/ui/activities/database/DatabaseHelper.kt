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
        insertDefaultItems(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS items")
        db.execSQL("DROP TABLE IF EXISTS reports")
        onCreate(db)
    }

    private fun insertDefaultItems(db: SQLiteDatabase) {
        val defaultItems = listOf(
            "मिरची पटना", "मिरची बेडगी", "मिरची काश्मिरी", "मिरची संकेश्वरी", "मिरची रेशमपट्टी",
            "धने ( कोथिंबीर )", "राई", "जिरा", "काळीमिरी", "दालचिनी ( तज )", "लवंग", "हिंग",
            "खसखस", "मेथी", "चणा", "बडीशोप", "मिक्स मसाला", "हळद राजापुरी", "हळद सेलम", "हळद निजाम",
            "वेलदोडा ( हिरवी वेलची )", "मसाला वेलची ( काली वेलची / मोठी वेलची )", "जयवंती ( जायपत्री / मायपत्री )",
            "रामपत्री", "बादियान ( कर्णफुल / चक्रीफूल )", "नागकेशर", "शहाजिरे", "त्रीफल  ( तीरफळ )",
            "दगडफूल", "तमालपत्र", "काबाबचिनी  ( कंकोळ )", "सुंठ", "जायफळ अखंड", "जायफळ सोललेली",
            "पिंपळी", "कलोंजी ( कांदाबी )"
        )

        for (item in defaultItems) {
            val values = ContentValues().apply {
                put("name", item)
                put("ratePerKg", 0.0) // Default price set to 0
            }
            db.insert("items", null, values)
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

    fun saveReport(filename: String, customer: String, amount: String, date: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("filename", filename)
            put("customer", customer)
            put("amount", amount)
            put("dateTime", date)
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

    fun deleteReport(item: ReportItem) {
        val db = writableDatabase

        // Define the WHERE clause to match both filename and datetimestamp
        val whereClause = "filename = ? AND dateTime = ?"
        val whereArgs = arrayOf(item.fileName, item.dateTime)

        // Perform the delete operation
        val result = db.delete("reports", whereClause, whereArgs)

        // Check if the deletion was successful
        if (result > 0) {
            Log.d("DatabaseHelper", "Report deleted successfully: ${item.fileName}, ${item.dateTime}")
        } else {
            Log.e("DatabaseHelper", "Failed to delete report: ${item.fileName}, ${item.dateTime}")
        }

        // Close the database connection
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "MasalaBilling.db"
        private const val DATABASE_VERSION = 2
    }
}
