package com.example.moneytracker.data

import com.example.moneytracker.data.entities.CategoryEntity
import com.example.moneytracker.data.entities.TransactionEntity

class DbConfig {
    companion object {
        const val DB_NAME = "app-money-tracker.db"
        const val DB_VERSION = 1

        //Category
        fun buildCategorySchema() = CategoryEntity.buildScheme().trimIndent()
        fun dropCategoryTable() = CategoryEntity.drop().trimIndent()
        //Transaction
        fun buildTransactionSchema() = TransactionEntity.buildScheme().trimIndent()
        fun dropTransactionTable() = TransactionEntity.drop().trimIndent()
    }
}