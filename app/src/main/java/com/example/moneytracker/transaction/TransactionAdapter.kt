package com.example.moneytracker.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.R

class TransactionAdapter(private val data: MutableList<Transaction>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class TransactionViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(data: Transaction){
            itemView.let {
                it.findViewById<TextView>(R.id.tvTransactionTitle).text = data.title
                it.findViewById<TextView>(R.id.tvTransactionDate).text = data.date
                it.findViewById<TextView>(R.id.tvTransactionMoney).apply {
                   when(data.type){
                       TransactionType.EXPENSE -> {
                           text = "- ${data.money}"
                           setTextColor(Color.RED)
                       }
                       TransactionType.ADD -> {
                           text = "+ ${data.money}"
                           setTextColor(resources.getColor(R.color.green))
                       }
                   }
                }
                it.findViewById<TextView>(R.id.tvTransactionUnit).text = data.unit
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent,false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is TransactionViewHolder){
            holder.bind(data[position])
        }
    }
}