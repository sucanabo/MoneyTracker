package com.example.moneytracker.features.transaction.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.R
import com.example.moneytracker.features.transaction.data.TransactionRepository
import com.example.moneytracker.features.transaction.domain.model.TransactionModel
import com.example.moneytracker.features.transaction.domain.model.TransactionType

class TransactionAdapter(
    private var data: MutableList<TransactionModel> = mutableListOf(),
    private val repo: TransactionRepository,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<TransactionModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: TransactionModel) {
            itemView.let {
                it.findViewById<TextView>(R.id.tvTransactionTitle).text = data.title
                it.findViewById<TextView>(R.id.tvTransactionDate).text = data.date
                it.findViewById<TextView>(R.id.tvTransactionMoney).apply {
                    when (data.type) {
                        TransactionType.EXPENSE -> {
                            text = "- $${data.money}"
                            setTextColor(Color.RED)
                        }

                        TransactionType.ADD -> {
                            text = "+ $${data.money}"
                            setTextColor(ResourcesCompat.getColor(resources,R.color.green,null))
                        }

                        else -> {}
                    }
                }
                it.findViewById<TextView>(R.id.tvTransactionUnit).text = data.unit
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TransactionViewHolder) {
            holder.bind(data[position])
        }
    }
}