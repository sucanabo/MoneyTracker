package com.example.moneytracker.data.repositories

import android.content.ContentValues
import com.example.moneytracker.data.MoneyTrackerDb
import com.example.moneytracker.data.entities.CategoryEntity
import com.example.moneytracker.domain.model.CategoryModel
import com.example.moneytracker.domain.repositories.ICategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val database: MoneyTrackerDb): ICategoryRepository {

    companion object {
        fun create(db: MoneyTrackerDb): CategoryRepository = CategoryRepository(db)
    }

    override suspend fun insert(model: CategoryModel):Boolean = withContext(Dispatchers.IO) {
        ContentValues().apply {
            put(CategoryEntity.COL_NAME, model.name)
            put(CategoryEntity.COL_IMG, model.imgPath)
        }.let {
            database.writableDatabase.insert(CategoryEntity.TABLE_NAME, null, it) > 0
        }
    }

   override suspend fun select(limit: Int?) = withContext(Dispatchers.IO) {
        val db = database.readableDatabase
        val cursor = db.query(
            CategoryEntity.TABLE_NAME,
            arrayOf(
                CategoryEntity.COL_ID,
                CategoryEntity.COL_NAME,
                CategoryEntity.COL_IMG,
            ),
            null,
            null,
            null,
            null,
            null,
            limit?.toString(),
        )
        val result = mutableListOf<CategoryModel>()
        cursor.let {
            if (cursor.moveToFirst()) {
                do {
                    val indexId = it.getColumnIndex(CategoryEntity.COL_ID)
                    val indexName = it.getColumnIndex(CategoryEntity.COL_NAME)
                    val indexImg = it.getColumnIndex(CategoryEntity.COL_IMG)

                    result.add(
                        CategoryModel(
                            id = it.getInt(indexId),
                            name = it.getString(indexName),
                            imgPath = it.getString(indexImg),
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        result
    }

    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        database.writableDatabase.delete(
            CategoryEntity.TABLE_NAME,
            "${CategoryEntity.COL_ID} = $id",
            null
        ) > 0
    }

    override suspend fun update(model: CategoryModel) = withContext(Dispatchers.IO) {
        database.writableDatabase.update(
            CategoryEntity.TABLE_NAME,
            ContentValues().apply {
                put(CategoryEntity.COL_ID, model.id)
                put(CategoryEntity.COL_NAME, model.name)
                put(CategoryEntity.COL_IMG, model.imgPath)
            },
            "${CategoryEntity.COL_ID} = ${model.id}",
            null
        ) > 0

    }
}