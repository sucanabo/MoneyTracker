package com.example.moneytracker

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.data.local.MoneyTrackerDb
import com.example.moneytracker.features.transaction.data.TransactionRepository
import com.example.moneytracker.features.transaction.domain.model.TransactionModel
import com.example.moneytracker.features.transaction.domain.model.TransactionType
import com.example.moneytracker.features.transaction.presentation.TransactionAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database = MoneyTrackerDb(applicationContext))
    }

    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout = LinearLayoutManager(applicationContext)
        transactionAdapter = TransactionAdapter(repo = transactionRepository)

        val decoration = DividerItemDecoration(this, linearLayout.orientation)

        ContextCompat.getDrawable(this, R.drawable.bg_divider)
        findViewById<RecyclerView>(R.id.rvTransaction).apply {
            layoutManager = linearLayout
            adapter = transactionAdapter
            addItemDecoration(decoration)

        }
        loadTransactionFromDb()

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

    }

    fun loadTransactionFromDb() {
        launch {
            delay(2000L)
            val list = transactionRepository.selectAll()
            withContext(Dispatchers.Main) {
                if (list.isEmpty()) {
                    displayEmptyTransaction()
                } else {
                    displayTransaction(list)
                }
            }
        }
    }

    private fun displayEmptyTransaction() {
        findViewById<ProgressBar>(R.id.loading).visibility = View.GONE
        findViewById<RecyclerView>(R.id.rvTransaction).visibility = View.GONE
        findViewById<TextView>(R.id.tvStatus).apply {
            visibility = View.VISIBLE
            text = "Empty transaction"
        }
    }

    private fun displayTransaction(listTransaction: MutableList<TransactionModel>) {
        findViewById<ProgressBar>(R.id.loading).visibility = View.GONE
        findViewById<TextView>(R.id.tvStatus).visibility = View.GONE

        findViewById<RecyclerView>(R.id.rvTransaction).visibility = View.VISIBLE
        transactionAdapter.setData(listTransaction)
    }

    fun mockTransactionList(): MutableList<TransactionModel> {
        val result = mutableListOf<TransactionModel>()
        repeat(3) {
            result.addAll(
                mutableListOf(
                    TransactionModel(
                        id = 0,
                        cateId = 0,
                        type = TransactionType.ADD,
                        title = "Lanh luong",
                        date = "20-10-2023",
                        money = 30.2f,
                        unit = "Cash"
                    ),
                    TransactionModel(
                        id = 1,
                        cateId = 2,
                        type = TransactionType.EXPENSE,
                        title = "Gym",
                        date = "22-10-2023",
                        money = 10.2f,
                        unit = "Cash"
                    ),
                    TransactionModel(
                        id = 2,
                        cateId = 0,
                        type = TransactionType.ADD,
                        title = "AFF",
                        date = "01-10-2023",
                        money = 5f,
                        unit = "Cash"
                    ),
                    TransactionModel(
                        id = 3,
                        cateId = 3,
                        type = TransactionType.EXPENSE,
                        title = "Buy Car",
                        date = "20-10-2023",
                        money = 3000.2f,
                        unit = "Cash"
                    ),

                    )
            )
        }
        return result
    }
}
