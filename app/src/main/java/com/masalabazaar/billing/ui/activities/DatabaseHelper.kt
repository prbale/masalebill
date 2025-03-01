package com.masalabazaar.billing.ui.activities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_ITEMS (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ratePerKg REAL)"
        db.execSQL(createTableQuery)
        seedDatabase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
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
            db.insert(TABLE_ITEMS, null, values)
        }
    }

    fun getItems(): List<BillItem> {
        val items = mutableListOf<BillItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ITEMS", null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val ratePerKg = cursor.getDouble(cursor.getColumnIndexOrThrow("ratePerKg"))
            items.add(BillItem(name, ratePerKg, 0.0))
        }
        cursor.close()
        return items
    }

    companion object {
        private const val DATABASE_NAME = "MasalaBilling.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ITEMS = "items"
    }
}
