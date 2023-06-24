package com.example.moneytracker.features.transaction.presentation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.moneytracker.R
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.data.MoneyTrackerRepository
import com.example.moneytracker.features.transaction.data.TransactionModel
import com.example.moneytracker.features.transaction.data.TransactionType
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class TransactionInputActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main

    private val repository: MoneyTrackerRepository by lazy {
        MoneyTrackerRepository.create(db = MoneyTrackerDb(applicationContext))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        var tranType: TransactionType = TransactionType.EXPENSE
        val tAmount = findViewById<ExtendedEditText>(R.id.tfe_tran_amount)
        val tTitle = findViewById<ExtendedEditText>(R.id.tfe_tran_title)
        val tDate = findViewById<ExtendedEditText>(R.id.tfe_tran_date)
        val tNote = findViewById<ExtendedEditText>(R.id.tfe_tran_note)

        val data: TransactionModel? = intent.getSerializableExtra("model", TransactionModel::class.java).also {
            if (it != null) {
                title = "Edit Transaction"
                tranType = it.type
                tAmount.setText(it.money.toString())
                tTitle.setText(it.title)
                tDate.setText(it.date)
                it.note?.let { noteTxt -> tNote.setText(noteTxt) }
                return@also
            }
            title = "New Transaction"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        findViewById<StickySwitch>(R.id.sw_tran).setA(object : StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                tranType = when (direction) {
                    StickySwitch.Direction.LEFT -> TransactionType.EXPENSE
                    StickySwitch.Direction.RIGHT -> TransactionType.ADD
                }
            }
        })
        findViewById<Button>(R.id.btn_tran_save).setOnClickListener {
            val tAmount = findViewById<ExtendedEditText>(R.id.tfe_tran_amount).text.toString()
            val tTitle = findViewById<ExtendedEditText>(R.id.tfe_tran_title).text.toString()
            val tDate = findViewById<ExtendedEditText>(R.id.tfe_tran_date).text.toString()
            val tNote = findViewById<ExtendedEditText>(R.id.tfe_tran_note).text.let {
                if (it.isNullOrBlank()) return@let null
                return@let it.toString()
            }

            Log.d("Debug", "amount ${tAmount.toFloat()}")

            launch {
                val model = TransactionModel(
                    money = tAmount.toFloat(),
                    title = tTitle,
                    date = tDate,
                    note = tNote,
                    type = tranType
                )
                Log.d("Debug", model.toString())
                repository.insert(
                    model
                )
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}