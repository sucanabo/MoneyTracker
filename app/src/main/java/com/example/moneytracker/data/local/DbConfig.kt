package com.example.moneytracker.data.local

import com.example.moneytracker.features.transaction.data.entity.TransactionEntity

class DbConfig {
    companion object {
        const val DB_NAME = "app-money-tracker.db"
        const val DB_VERSION = 1

        fun buildSchema() = TransactionEntity.buildScheme().trimIndent()
        fun dropAllTable() = TransactionEntity.drop().trimIndent()
    }
}