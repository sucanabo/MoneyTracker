package com.example.moneytracker.ui.transaction

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneytracker.R
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.domain.model.CategoryModel
import com.example.moneytracker.domain.model.TransactionModel
import com.example.moneytracker.domain.model.TransactionType
import com.example.moneytracker.providers.*
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.carbonylgroup.textfieldboxes.ExtendedEditText
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes
import java.text.SimpleDateFormat
import java.time.LocalDate

class TransactionInputActivity : AppCompatActivity(), CoroutineScope, DatePickerDialog.OnDateSetListener {

    private val job = Job()
    override val coroutineContext
        get() = job + Dispatchers.Main

    private val database = MoneyTrackerDb(applicationContext)

    private lateinit var btnDelete: Button
    private lateinit var btnSave: Button
    private lateinit var swTran: StickySwitch

    private lateinit var tfAmount: ExtendedEditText
    private lateinit var tfCate: ExtendedEditText
    private lateinit var tfDate: ExtendedEditText
    private lateinit var tfNote: ExtendedEditText

    private var transactionDate: LocalDate = LocalDate.now()
    private var isAllFieldValidate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        var tranType: TransactionType = TransactionType.EXPENSE

        swTran = findViewById(R.id.sw_tran)
        tfAmount = findViewById(R.id.tfe_tran_amount)
        tfCate = findViewById(R.id.tfe_tran_title)
        tfDate = findViewById(R.id.tfe_tran_date)
        tfNote = findViewById(R.id.tfe_tran_note)
        btnSave = findViewById(R.id.btn_tran_save)
        btnDelete = findViewById(R.id.btn_tran_delete)

        findViewById<TextFieldBoxes>(R.id.tfb_tran_date)?.endIconImageButton?.setOnClickListener { showDatePicker() }

        val initData = intent.getSerializableExtra(AppKeys.Argument.TRANSACTION_ID) as Int?
        initData.let { model ->
            if (model != null) {
                tranType = model.type
                runCatching {
                    transactionDate = LocalDate.parse(model.date)
                }
                title = "Edit Transaction"
                tfAmount.setText(model.money.toString())
//                tfName.setText(model.title)
                model.note?.let { noteTxt -> tfNote.setText(noteTxt) }
                btnDelete.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        launch {
                            if (database.tranRepo.delete(model.id)) setResult(RESULT_OK) else setResult(0)
                            calculateDeleteMoney(tranType, model.money)
                            finish()
                        }
                    }
                }
                return@let
            }
            title = "New Transaction"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        swTran.apply {
            setDirection(
                when (tranType) {
                    TransactionType.ADD -> {
                        rightIcon?.setTint(Color.WHITE)
                        StickySwitch.Direction.RIGHT
                    }

                    else -> {
                        leftIcon?.setTint(Color.WHITE)
                        StickySwitch.Direction.LEFT
                    }
                }
            )
            setA(object : StickySwitch.OnSelectedChangeListener {
                override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                    when (direction) {
                        StickySwitch.Direction.LEFT -> {
                            tranType = TransactionType.EXPENSE
                            leftIcon?.setTint(Color.WHITE)
                            rightIcon?.setTint(Color.BLACK)
                        }

                        StickySwitch.Direction.RIGHT -> {
                            tranType = TransactionType.ADD
                            rightIcon?.setTint(Color.WHITE)
                            leftIcon?.setTint(Color.BLACK)
                        }
                    }
                }
            })
        }
        btnSave.setOnClickListener {
            isAllFieldValidate = validateTransaction()
            if (!isAllFieldValidate) {
                return@setOnClickListener
            }
            launch {
                val model = TransactionModel(
                    money = tfAmount.text.toString().toFloat(),
                    category = CategoryModel(

                    ),
                    date = tfDate.text.toString(),
                    note = tfNote.text.let { if (it.isNullOrBlank()) null else it.toString() },
                    type = tranType
                )
                Log.d("Debug", model.toString())
                try {
                    if (initData != null) {
                        //Edit transaction
                        database.tranRepo.update(model)
                        calculateEditMoney(tranType, initData.money, model.money)
                    } else {
                        //Add new transaction
                        database.tranRepo.insert(model)
                        calculateAddMoney(tranType, model.money)

                    }
                } catch (e: Exception) {
                    Log.d("DEBUG", "Error save button \n $e")
                    setResult(0)
                    finish()
                }
                setResult(RESULT_OK)
                finish()
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun validateTransaction(): Boolean {
        var hasBeforeFocus: Boolean
        val amountValid = findViewById<TextFieldBoxes>(R.id.tfb_tran_amount).let {
            if (it == null) return@let true
            if (tfAmount.text.isEmpty()) {
                it.setError("Please fill amount.", true)
            }
            !it.isOnError
        }.also { hasBeforeFocus = it }
        val nameValid = findViewById<TextFieldBoxes>(R.id.tfb_tran_title).let { box ->
            if (box == null) return@let true
            tfCate.let {
                if (it.text.isEmpty()) box.setError("Please fill transaction name.", hasBeforeFocus)
                else if (it.text.length < 3) box.setError("Transaction name at least 3 characters.", true)
                else if (it.text.length > 255) box.setError("Transaction name maximum 255 characters.", true)
            }
            !box.isOnError
        }.also { hasBeforeFocus = it }
        val validDate = findViewById<TextFieldBoxes>(R.id.tfb_tran_date).let { box ->
            if (box == null) return@let true
            runCatching {
                SimpleDateFormat("dd/mm/yyyy").parse(tfDate.text.toString())
            }.getOrNull().also { date ->
                if (date == null) box.setError("Invalid date format", hasBeforeFocus)
            }
            !box.isOnError
        }
        Log.d("DEBUG", "Form valid: $amountValid - $nameValid")
        return amountValid && nameValid && validDate
    }

    private fun calculateAddMoney(type: TransactionType, money: Float) {
        when (type) {
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_EXPENSE, (it + money))
                }
            }

            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_ADD, (it + money))
                }
            }

            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateDeleteMoney(type: TransactionType, money: Float) {
        when (type) {
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_EXPENSE, (it - money))
                }
            }

            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_ADD, (it - money))
                }
            }

            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateEditMoney(type: TransactionType, oldMoney: Float, money: Float) {
        when (type) {
            TransactionType.EXPENSE -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_EXPENSE, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_EXPENSE, (it + money - oldMoney))
                }
            }

            TransactionType.ADD -> {
                SharePrefHelper.create(this)?.get(AppKeys.SharePref.MONEY_ADD, 0f)?.also {
                    SharePrefHelper.create(this)?.put(AppKeys.SharePref.MONEY_ADD, (it + money - oldMoney))
                }
            }

            TransactionType.UNKNOWN -> {
                Toast.makeText(this, "Fail to calculate money! - Unknown type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            this,
            transactionDate.year,
            transactionDate.month.value,
            transactionDate.dayOfMonth
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        if (p0 == null) return
        val str = p3.toString().padStart(2, '0') + "/" +
                p2.toString().padStart(2, '0') + "/" +
                p1.toString()
        tfDate.setText(str)
    }

}