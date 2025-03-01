package com.masalabazaar.billing.ui.activities

data class BillItem(
    val name: String,
    val ratePerKg: Double,
    var quantity: Double
)
