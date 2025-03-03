package com.masalabazaar.billing.ui.activities.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.masalabazaar.billing.R
import com.masalabazaar.billing.ui.activities.data.ReportItem
import java.text.SimpleDateFormat
import java.util.Date

class HistoryAdapter(
    private val context: Context,
    private val reportList: List<ReportItem>,
    private val onItemClick: (ReportItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val report = reportList[position]
        holder.customerName.text = report.customerName.trim().replace("_", " ")
        holder.date.text = "Date: ${report.dateTime.toLong().displayFormattedDate()}"
        holder.amount.text = "Total: ${report.amount}"

        holder.cardView.setOnClickListener {
            onItemClick(report)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun Long.displayFormattedDate(): String {
        val currentTimeMillis = this
        val date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("dd MMMM yyyy, h:mm a")
        val formattedDate = sdf.format(date)
        return formattedDate
    }

    override fun getItemCount(): Int = reportList.size

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val customerName: TextView = view.findViewById(R.id.reportCustomerName)
        val date: TextView = view.findViewById(R.id.reportDate)
        val amount: TextView = view.findViewById(R.id.reportAmount)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }
}
