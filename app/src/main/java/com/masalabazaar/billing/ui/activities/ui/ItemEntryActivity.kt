package com.masalabazaar.billing.ui.activities.ui

import android.R.attr
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.data.BillItem
import com.masalabazaar.billing.ui.activities.database.DatabaseHelper


class ItemEntryActivity : AppCompatActivity() {

    private lateinit var itemContainer: GridLayout
    private lateinit var saveButton: Button
    private val itemInputs = mutableListOf<Pair<TextView, EditText>>() // Pair(ItemName, RatePerKg)
    private lateinit var dbHelper: DatabaseHelper
    private var existingItems: List<BillItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_entry)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Enter Details"

        itemContainer = findViewById(R.id.itemContainer)
        saveButton = findViewById(R.id.save_btn)
        dbHelper = DatabaseHelper(this)

        loadExistingItems() // Load data from the database

        // Dynamically create 30 input fields side by side
        for (i in 1..30) {
            val itemNameInput = TextView(this).apply {
                hint = "Item Name $i"
                textSize = 20f
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(0, 1f) // First Column
                }
            }
            val itemRateInput = EditText(this).apply {
                hint = "Price per kg"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                textSize = 16f
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(1, 1f) // Second Column
                }
                setSelectAllOnFocus(true)
            }

            // Populate fields if data exists in the database
            if (i - 1 < existingItems.size) {
                itemNameInput.setText(existingItems[i - 1].name)
                itemRateInput.setText(existingItems[i - 1].ratePerKg.toString())
            }

            itemContainer.addView(itemNameInput)
            itemContainer.addView(itemRateInput)
            itemInputs.add(Pair(itemNameInput, itemRateInput))
        }

        saveButton.setOnClickListener {
            saveItemsToDatabase()
            setResult(Activity.RESULT_OK) // Notify MainActivity that data is saved
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.getLayoutParams() is MarginLayoutParams) {
            (view.getLayoutParams() as MarginLayoutParams).setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    private fun loadExistingItems() {
        existingItems = dbHelper.getItems()
    }

    private fun saveItemsToDatabase() {
        val itemsList = mutableListOf<BillItem>()

        for ((nameInput, rateInput) in itemInputs) {
            val name = nameInput.text.toString().trim()
            val rate = rateInput.text.toString().trim().toDoubleOrNull()

            if (name.isNotEmpty() && rate != null) {
                itemsList.add(BillItem(name, rate, 0.0))
            }
        }

        if (itemsList.isNotEmpty()) {
            dbHelper.saveItems(itemsList) // Save updated items
            Toast.makeText(this, "Items saved successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter valid item names and prices", Toast.LENGTH_SHORT).show()
        }
    }
}
