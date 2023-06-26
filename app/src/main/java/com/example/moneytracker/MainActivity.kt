package com.example.moneytracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.data.MoneyTrackerRepository
import com.example.moneytracker.features.transaction.data.TransactionModel
import com.example.moneytracker.features.transaction.data.TransactionType
import com.example.moneytracker.features.transaction.presentation.OnClickItem
import com.example.moneytracker.features.transaction.presentation.TransactionAdapter
import com.example.moneytracker.features.transaction.presentation.TransactionInputActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main

    private val repository: MoneyTrackerRepository by lazy {
        MoneyTrackerRepository.create(db = MoneyTrackerDb(applicationContext))
    }

    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactionAdapter = TransactionAdapter(
            repo = repository,
            onClickItem = transactionItemClick
        )

        initTransaction()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

    }

    private val transactionItemClick: OnClickItem = {
        Intent(this, TransactionInputActivity::class.java).apply {
            putExtra("model", it)
        }.also {
            activityTransactionAddResult.launch(it)
        }
    }

    private fun initTransaction() {
        val linearLayout = LinearLayoutManager(applicationContext)
        val decoration = DividerItemDecoration(this, linearLayout.orientation)

        ContextCompat.getDrawable(this, R.drawable.bg_divider)
        findViewById<RecyclerView>(R.id.rvTransaction).apply {
            layoutManager = linearLayout
            adapter = transactionAdapter
            addItemDecoration(decoration)

        }
        findViewById<ExtendedFloatingActionButton>(R.id.fab_add_transaction).setOnClickListener {
            val intent = Intent(this, TransactionInputActivity::class.java)
            activityTransactionAddResult.launch(intent)
        }
        loadTransactionFromDb(true)
    }

    private val activityTransactionAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("debug", "result code ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "result from ADDTRANSACTION", Toast.LENGTH_SHORT).show()
                loadTransactionFromDb(false)
            }
        }

    private fun loadTransactionFromDb(enableDelay: Boolean = false) {
        launch {
            if (enableDelay) {
                delay(2000L)
            }
            val list = repository.selectAll()
            withContext(Dispatchers.Main) {
                if (list.isEmpty()) {
                    displayEmptyTransaction()
                } else {
                    Log.d("DEBUG", "list tran")
                    list.forEach {
                        Log.d("DEBUG", it.toString())
                    }
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
