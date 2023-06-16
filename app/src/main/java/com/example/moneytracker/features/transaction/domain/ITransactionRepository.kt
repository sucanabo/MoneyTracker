package com.example.moneytracker.features.transaction.domain

import com.example.moneytracker.features.transaction.domain.model.TransactionModel

interface ITransactionRepository {
    suspend fun insert(model: TransactionModel): Unit

    suspend fun selectAll(): List<TransactionModel>

    suspend fun delete(id: Int): Boolean

    suspend fun update(model: TransactionModel): Boolean
}