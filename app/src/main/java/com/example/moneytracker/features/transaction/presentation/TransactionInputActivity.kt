package com.example.moneytracker.features.transaction.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneytracker.R
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.data.MoneyTrackerRepository
import com.example.moneytracker.features.transaction.data.TransactionModel
import com.example.moneytracker.features.transaction.data.TransactionType
import com.example.moneytracker.providers.SharePrefHelper
import com.example.moneytracker.providers.SharePrefKey
import com.example.moneytracker.providers.get
import com.example.moneytracker.providers.put
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        var tranType: TransactionType = TransactionType.EXPENSE
        val tAmount = findViewById<ExtendedEditText>(R.id.tfe_tran_amount)
        val tTitle = findViewById<ExtendedEditText>(R.id.tfe_tran_title)
        val tDate = findViewById<ExtendedEditText>(R.id.tfe_tran_date)
        val tNote = findViewById<ExtendedEditText>(R.id.tfe_tran_note)

        val initData = intent.getSerializableExtra("model") as TransactionModel?
        initData.let { model ->
            if (model != null) {
                title = "Edit Transaction"
                tranType = model.type
                tAmount.setText(model.money.toString())
                tTitle.setText(model.title)
                tDate.setText(model.date)
                model.note?.let { noteTxt -> tNote.setText(noteTxt) }
                findViewById<Button>(R.id.btn_tran_delete)?.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        launch {
                            if (repository.delete(model.id)) setResult(RESULT_OK) else setResult(0)
                            calculateDeleteMoney(tranType,model.money)
                            finish()
                        }
                    }
                }
                return@let
            }
            title = "New Transaction"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        findViewById<StickySwitch>(R.id.sw_tran).apply {
            setDirection(
                when (tranType) {
                    TransactionType.ADD -> StickySwitch.Direction.RIGHT
                    else -> StickySwitch.Direction.LEFT
                }
            )
            setA(object : StickySwitch.OnSelectedChangeListener {
                override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                    tranType = when (direction) {
                        StickySwitch.Direction.LEFT -> TransactionType.EXPENSE
                        StickySwitch.Direction.RIGHT -> TransactionType.ADD
                    }
                }
            })
        }
        findViewById<Button>(R.id.btn_tran_save).setOnClickListener {
            launch {
                val model = TransactionModel(
                    money = tAmount.text.toString().toFloat(),
                    title = tTitle.text.toString(),
                    date = tDate.text.toString(),
                    note = tNote.text.let { if (it.isNullOrBlank()) null else it.toString() },
                    type = tranType
                )
                Log.d("Debug", model.toString())
                try {
                    if (initData != null) {
                        //Edit transaction
                        repository.update(model)
                        calculateEditMoney(tranType,initData.money, model.money)
                    } else {
                        //Add new transaction
                        repository.insert(model)
                        calculateAddMoney(tranType,model.money)

                    }
                } catch (e:Exception){
                    Log.d("DEBUG", "Error save button \n $e")
                    setResult(0)
                    finish()
                }
                setResult(Activity.RESULT_OK)
                finish()
            }

        }
    }

    private fun calculateAddMoney(type: TransactionType, money: Float){
        when(type){
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_EXPENSE, (it + money) )
                }
            }
            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_ADD, (it + money))
                }
            }
            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun calculateDeleteMoney(type: TransactionType, money: Float){
        when(type){
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_EXPENSE, (it - money) )
                }
            }
            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_ADD, (it - money))
                }
            }
            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateEditMoney(type: TransactionType,oldMoney: Float, money: Float){
        when(type){
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_EXPENSE, (it + money - oldMoney ) )
                }
            }
            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(SharePrefKey.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(SharePrefKey.MONEY_ADD, (it + money - oldMoney))
                }
            }
            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
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