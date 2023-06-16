package com.example.moneytracker.features.transaction.data

import android.annotation.SuppressLint
import android.content.ContentValues
import com.example.moneytracker.data.local.MoneyTrackerDb
import com.example.moneytracker.features.transaction.data.entity.TransactionEntity
import com.example.moneytracker.features.transaction.domain.ITransactionRepository
import com.example.moneytracker.features.transaction.domain.model.TransactionModel
import com.example.moneytracker.features.transaction.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val database: MoneyTrackerDb): ITransactionRepository {
    companion object{
        fun init(database: MoneyTrackerDb): TransactionRepository = TransactionRepository(database)
    }
    override suspend fun insert(model: TransactionModel):Unit = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        ContentValues().apply {
            put(TransactionEntity.COL_ID, model.id)
            put(TransactionEntity.COL_CATE_ID, model.cateId)
            put(TransactionEntity.COL_TYPE, model.type.value)
            put(TransactionEntity.COL_TITLE, model.title)
            put(TransactionEntity.COL_DATE, model.date)
            put(TransactionEntity.COL_MONEY, model.money)
            put(TransactionEntity.COL_UNIT, model.unit)
            put(TransactionEntity.COL_NOTE, model.note)
        }.also {
            db.insert(TransactionEntity.TABLE_NAME, null, it)
        }
    }

    @SuppressLint("Recycle")
    override suspend fun selectAll() = withContext(Dispatchers.IO) {
        val db = database.readableDatabase
        val cursor = db.query(
            TransactionEntity.TABLE_NAME,
            arrayOf(
                TransactionEntity.COL_ID,
                TransactionEntity.COL_CATE_ID,
                TransactionEntity.COL_TYPE,
                TransactionEntity.COL_TITLE,
                TransactionEntity.COL_DATE,
                TransactionEntity.COL_MONEY,
                TransactionEntity.COL_UNIT,
                TransactionEntity.COL_NOTE,
            ),
            null,
            null,
            null,
            null,
            null,
            null,
        )
        val result = mutableListOf<TransactionModel>()
        cursor.let {
            if (cursor.moveToFirst()) {
                do {
                    val indexId = it.getColumnIndex(TransactionEntity.COL_ID)
                    val indexCateId = it.getColumnIndex(TransactionEntity.COL_CATE_ID)
                    val indexType = it.getColumnIndex(TransactionEntity.COL_TYPE)
                    val indexTitle = it.getColumnIndex(TransactionEntity.COL_TITLE)
                    val indexDate = it.getColumnIndex(TransactionEntity.COL_DATE)
                    val indexMoney = it.getColumnIndex(TransactionEntity.COL_MONEY)
                    val indexUnit = it.getColumnIndex(TransactionEntity.COL_UNIT)
                    val indexNote = it.getColumnIndex(TransactionEntity.COL_NOTE)

                    result.add(
                        TransactionModel(
                            id = it.getInt(indexId),
                            cateId = it.getInt(indexCateId),
                            type = TransactionType.convertFromString(it.getString(indexType)),
                            title = it.getString(indexTitle),
                            date = it.getString(indexDate),
                            money = it.getFloat(indexMoney),
                            unit = it.getString(indexUnit),
                            note = it.getString(indexNote),
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        result
    }

    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        database.writableDatabase.delete(
            TransactionEntity.TABLE_NAME,
            "${TransactionEntity.COL_ID} = $id",
            null
        ) > 0
    }

    override suspend fun update(model: TransactionModel) = withContext(Dispatchers.IO) {
        database.writableDatabase.update(
            TransactionEntity.TABLE_NAME,
            ContentValues().apply {
                put(TransactionEntity.COL_ID, model.id)
                put(TransactionEntity.COL_CATE_ID, model.cateId)
                put(TransactionEntity.COL_TYPE, model.type.value)
                put(TransactionEntity.COL_TITLE, model.title)
                put(TransactionEntity.COL_DATE, model.date)
                put(TransactionEntity.COL_MONEY, model.money)
                put(TransactionEntity.COL_UNIT, model.unit)
                put(TransactionEntity.COL_NOTE, model.note)
            },
            "${TransactionEntity.COL_ID} = ${model.id}",
            null
        ) > 0

    }
}