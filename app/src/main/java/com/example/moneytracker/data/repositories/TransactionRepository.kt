package com.example.moneytracker.data.repositories

import android.content.ContentValues
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.data.entities.CategoryEntity
import com.example.moneytracker.data.entities.TransactionEntity
import com.example.moneytracker.domain.model.CategoryModel
import com.example.moneytracker.domain.model.TransactionModel
import com.example.moneytracker.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val database: MoneyTrackerDb) {
    companion object {
        fun create(db: MoneyTrackerDb): TransactionRepository = TransactionRepository(db)
    }

    suspend fun insert(model: TransactionModel): Unit = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        ContentValues().apply {
            put(TransactionEntity.COL_CATE_ID, model.category.id)
            put(TransactionEntity.COL_TYPE, model.type.value)
            put(TransactionEntity.COL_DATE, model.date)
            put(TransactionEntity.COL_MONEY, model.money)
            put(TransactionEntity.COL_UNIT, model.unit)
            put(TransactionEntity.COL_NOTE, model.note)
        }.also {
            db.insert(TransactionEntity.TABLE_NAME, null, it)
        }
    }

    suspend fun select(limit: Int? = null) = withContext(Dispatchers.IO) {
        val db = database.readableDatabase
        val cursor = db.query(
            "${TransactionEntity.TABLE_NAME}, ${CategoryEntity.TABLE_NAME}",
            arrayOf(
                CategoryEntity.COL_IMG,
                CategoryEntity.COL_NAME,
                "${TransactionEntity.TABLE_NAME}.${TransactionEntity.COL_ID}",
                TransactionEntity.COL_CATE_ID,
                TransactionEntity.COL_TYPE,
                TransactionEntity.COL_DATE,
                TransactionEntity.COL_MONEY,
                TransactionEntity.COL_UNIT,
                TransactionEntity.COL_NOTE,
            ),
            "${TransactionEntity.TABLE_NAME}.${TransactionEntity.COL_CATE_ID} = ${CategoryEntity.TABLE_NAME}.${CategoryEntity.COL_ID}",
            null,
            null,
            null,
            "${TransactionEntity.COL_DATE} DESC",
            limit?.toString(),
        )
        val result = mutableListOf<TransactionModel>()
        cursor.let {
            if (cursor.moveToFirst()) {
                do {
                    val indexCateImg = it.getColumnIndex(CategoryEntity.COL_IMG)
                    val indexCateName = it.getColumnIndex(CategoryEntity.COL_NAME)
                    val indexId = it.getColumnIndex(TransactionEntity.COL_ID)
                    val indexCateId = it.getColumnIndex(TransactionEntity.COL_CATE_ID)
                    val indexType = it.getColumnIndex(TransactionEntity.COL_TYPE)
                    val indexDate = it.getColumnIndex(TransactionEntity.COL_DATE)
                    val indexMoney = it.getColumnIndex(TransactionEntity.COL_MONEY)
                    val indexUnit = it.getColumnIndex(TransactionEntity.COL_UNIT)
                    val indexNote = it.getColumnIndex(TransactionEntity.COL_NOTE)

                    result.add(
                        TransactionModel(
                            id = it.getInt(indexId),
                            category = CategoryModel(
                                id = it.getInt(indexCateId),
                                name = it.getString(indexCateName),
                                imgPath = it.getString(indexCateImg),
                            ),
                            type = TransactionType.convertFromString(it.getString(indexType)),
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

    suspend fun selectById(id: Int) = withContext(Dispatchers.IO) {
        database.readableDatabase.query(
            TransactionEntity.TABLE_NAME,
            arrayOf(),
            null,
            null,
            null,
            null,
            null,
            null,
        )?.let { cursor ->
            if (cursor.moveToFirst()) {
                val indexCateImg = cursor.getColumnIndex(CategoryEntity.COL_IMG)
                val indexCateName = cursor.getColumnIndex(CategoryEntity.COL_NAME)
                val indexId = cursor.getColumnIndex(TransactionEntity.COL_ID)
                val indexCateId = cursor.getColumnIndex(TransactionEntity.COL_CATE_ID)
                val indexType = cursor.getColumnIndex(TransactionEntity.COL_TYPE)
                val indexDate = cursor.getColumnIndex(TransactionEntity.COL_DATE)
                val indexMoney = cursor.getColumnIndex(TransactionEntity.COL_MONEY)
                val indexUnit = cursor.getColumnIndex(TransactionEntity.COL_UNIT)
                val indexNote = cursor.getColumnIndex(TransactionEntity.COL_NOTE)
                return@withContext TransactionModel(
                    id = cursor.getInt(indexId),
                    category = CategoryModel(
                        id = cursor.getInt(indexCateId),
                        name = cursor.getString(indexCateName),
                        imgPath = cursor.getString(indexCateImg),
                    ),
                    type = TransactionType.convertFromString(cursor.getString(indexType)),
                    date = cursor.getString(indexDate),
                    money = cursor.getFloat(indexMoney),
                    unit = cursor.getString(indexUnit),
                    note = cursor.getString(indexNote),
                )
            }
            return@withContext null
        }
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        database.writableDatabase.delete(
            TransactionEntity.TABLE_NAME,
            "${TransactionEntity.COL_ID} = $id",
            null
        ) > 0
    }

    suspend fun update(model: TransactionModel) = withContext(Dispatchers.IO) {
        database.writableDatabase.update(
            TransactionEntity.TABLE_NAME,
            ContentValues().apply {
                put(TransactionEntity.COL_ID, model.id)
                put(TransactionEntity.COL_CATE_ID, model.category.id)
                put(TransactionEntity.COL_TYPE, model.type.value)
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