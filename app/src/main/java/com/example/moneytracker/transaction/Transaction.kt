package com.example.moneytracker.transaction

enum class TransactionType {EXPENSE,ADD}
data class Transaction(
    val id: Int,
    val cateId: Int,
    val type: TransactionType,
    val title: String,
    val money: Float,
    val unit: String,
    val date: String,
    val note: String? = null,
)
