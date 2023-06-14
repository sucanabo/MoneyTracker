package com.example.moneytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.transaction.Transaction
import com.example.moneytracker.transaction.TransactionAdapter
import com.example.moneytracker.transaction.TransactionType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout = LinearLayoutManager(applicationContext)

        val decoration = DividerItemDecoration(this,linearLayout.orientation)
        val transactionAdapter = TransactionAdapter(mockTransactionList())

        ContextCompat.getDrawable(this, R.drawable.bg_divider)
        findViewById<RecyclerView>(R.id.rvTransaction).apply {
            layoutManager = linearLayout
            adapter = transactionAdapter
            addItemDecoration(decoration)

        }

    }
}

fun mockTransactionList(): MutableList<Transaction>{
    return  mutableListOf(
        Transaction(
            id =  0,
            cateId = 0,
            type = TransactionType.ADD,
            title = "Lanh luong",
            date = "20-10-2023",
            money = 30.2f,
            unit = "Cash"
        ),
        Transaction(
            id =  1,
            cateId = 2,
            type = TransactionType.EXPENSE,
            title = "Gym",
            date = "22-10-2023",
            money = 10.2f,
            unit = "Cash"
        ),
        Transaction(
            id =  2,
            cateId = 0,
            type = TransactionType.ADD,
            title = "AFF",
            date = "01-10-2023",
            money = 5f,
            unit = "Cash"
        ),
        Transaction(
            id =  3,
            cateId = 3,
            type = TransactionType.EXPENSE,
            title = "Buy Car",
            date = "20-10-2023",
            money = 3000.2f,
            unit = "Cash"
        ),

        )
}