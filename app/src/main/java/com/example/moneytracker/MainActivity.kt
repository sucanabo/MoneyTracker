package com.example.moneytracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.domain.model.TransactionModel
import com.example.moneytracker.providers.AppKeys
import com.example.moneytracker.providers.SharePrefHelper
import com.example.moneytracker.providers.get
import com.example.moneytracker.ui.transaction.OnClickItem
import com.example.moneytracker.ui.transaction.TransactionAdapter
import com.example.moneytracker.ui.transaction.TransactionInputActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main


    private val database = MoneyTrackerDb(applicationContext)


    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactionAdapter = TransactionAdapter(onClickItem = transactionItemClick)

        initTransaction()
        loadTrackingMoney()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

    }

    private val transactionItemClick: OnClickItem = {
        Intent(this, TransactionInputActivity::class.java).apply {
            putExtra(AppKeys.Argument.TRANSACTION_ID, it)
        }.also {
            activityTransactionAddResult.launch(it)
        }
    }

    private fun loadTrackingMoney() {
        SharePrefHelper.create(this).apply {
            this?.get(AppKeys.SharePref.MONEY_EXPENSE, 0f)?.let {
                Log.d("DEBUG", "money expense load: $it")
                findViewById<TextView>(R.id.tvExpense).text = "$ $it"
            }
            this?.get(AppKeys.SharePref.MONEY_ADD, 0f)?.let {
                Log.d("DEBUG", "money add load: $it")
                findViewById<TextView>(R.id.tvAddMoney).text = "$ $it"
            }
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
                loadTransactionFromDb(false)
                loadTrackingMoney()
            }
        }

    private fun loadTransactionFromDb(enableDelay: Boolean = false) {
        launch {
            if (enableDelay) {
                delay(2000L)
            }
            val tranList = database.tranRepo.select()
            withContext(Dispatchers.Main) {
                if (tranList.isEmpty()) {
                    displayEmptyTransaction()
                } else {
                    Log.d("DEBUG", "list tran")
                    tranList.forEach {
                        Log.d("DEBUG", it.toString())
                    }
                    displayTransaction(tranList)
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
}
