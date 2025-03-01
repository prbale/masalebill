package com.masalabazaar.billing.ui.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.addTextChangedListener
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.data.BillItem

class BillAdapter(private val items: MutableList<BillItem>, private val updateTotal: () -> Unit) :
    RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.rate.text = "₹${item.ratePerKg}"    
        holder.quantity.setText(item.quantity.toString())

        holder.quantity.addTextChangedListener {
            item.quantity = it.toString().toDoubleOrNull() ?: 0.0
            updateTotal()
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemName)
        val rate: TextView = view.findViewById(R.id.itemRate)
        val quantity: EditText = view.findViewById(R.id.itemQuantity)
    }
}
