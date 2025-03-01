package com.masalabazaar.billing.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.masalabazaar.billing.R

class ItemEntryActivity : AppCompatActivity() {

    private lateinit var itemContainer: LinearLayout
    private lateinit var saveButton: Button
    private val itemInputs = mutableListOf<Pair<EditText, EditText>>() // Pair(ItemName, RatePerKg)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_entry)

        itemContainer = findViewById(R.id.item_container)
        saveButton = findViewById(R.id.save_btn)

        // Dynamically create 30 input fields
        for (i in 1..30) {
            val itemNameInput = EditText(this).apply {
                hint = "Item Name $i"
                textSize = 16f
            }
            val itemRateInput = EditText(this).apply {
                hint = "Price per kg"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                textSize = 16f
            }

            itemContainer.addView(itemNameInput)
            itemContainer.addView(itemRateInput)
            itemInputs.add(Pair(itemNameInput, itemRateInput))
        }

        saveButton.setOnClickListener {
            saveItemsToDatabase()
            setResult(Activity.RESULT_OK) // Notify MainActivity that data is saved
        }
    }

    private fun saveItemsToDatabase() {
        val dbHelper = DatabaseHelper(this)
        val itemsList = mutableListOf<BillItem>()

        for ((nameInput, rateInput) in itemInputs) {
            val name = nameInput.text.toString().trim()
            val rate = rateInput.text.toString().trim().toDoubleOrNull()

            if (name.isNotEmpty() && rate != null) {
                itemsList.add(BillItem(name, rate, 0.0))
            }
        }

        if (itemsList.isNotEmpty()) {
            dbHelper.saveItems(itemsList)
            Toast.makeText(this, "Items saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please enter valid item names and prices", Toast.LENGTH_SHORT).show()
        }
    }
}

