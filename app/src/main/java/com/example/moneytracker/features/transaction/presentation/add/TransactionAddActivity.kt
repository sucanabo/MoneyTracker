package com.example.moneytracker.features.transaction.presentation.add

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.moneytracker.R
import com.example.moneytracker.data.local.MoneyTrackerDb
import com.example.moneytracker.features.transaction.data.TransactionRepository
import com.example.moneytracker.features.transaction.domain.model.TransactionType
import com.example.moneytracker.features.transaction.presentation.adapters.TransactionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class TransactionAddActivity : AppCompatActivity(),
    CoroutineScope,
    TransactionInputFragment.TransactionInputArgs {

    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database = MoneyTrackerDb(applicationContext))
    }

    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        (supportFragmentManager.findFragmentById(R.id.frag_tran_input) as TransactionInputFragment).apply {
            this.setTransactionInputArgs(args = this@TransactionAddActivity)
        }

        title = "New Transaction"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override var transactionType: TransactionType = TransactionType.EXPENSE
    override fun onSwitchChanged(transactionType: TransactionType) {
        Log.d("DEBUG", "hihi")
    }
}