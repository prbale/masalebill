package com.masalabazaar.billing.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.masalabazaar.billing.R

class ItemEntryActivity : AppCompatActivity() {

    private lateinit var itemContainer: GridLayout
    private lateinit var saveButton: Button
    private val itemInputs = mutableListOf<Pair<EditText, EditText>>() // Pair(ItemName, RatePerKg)
    private lateinit var dbHelper: DatabaseHelper
    private var existingItems: List<BillItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_entry)

        itemContainer = findViewById(R.id.itemContainer)
        saveButton = findViewById(R.id.save_btn)
        dbHelper = DatabaseHelper(this)

        loadExistingItems() // Load data from the database

        // Dynamically create 30 input fields side by side
        for (i in 1..30) {
            val itemNameInput = EditText(this).apply {
                hint = "Item Name $i"
                textSize = 16f
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
