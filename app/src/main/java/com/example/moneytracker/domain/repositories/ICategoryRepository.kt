package com.example.moneytracker.domain.repositories

import com.example.moneytracker.domain.model.CategoryModel

interface ICategoryRepository {
    suspend fun insert(model: CategoryModel):Boolean
    suspend fun select(limit: Int?= null): MutableList<CategoryModel>
    suspend fun update(model: CategoryModel): Boolean
    suspend fun delete(id: Int): Boolean
}