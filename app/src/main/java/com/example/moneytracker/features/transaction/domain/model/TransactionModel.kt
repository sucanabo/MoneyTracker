package com.example.moneytracker.features.transaction.domain.model

enum class TransactionType(val value: String) {
    EXPENSE("E"),
    ADD("A"),
    UNKNOWN("");

    companion object {
        fun convertFromString(str: String): TransactionType {
            return when (str) {
                "E" -> EXPENSE
                "A" -> ADD
                else -> UNKNOWN
            }
        }
    }
}

data class TransactionModel(
    val id: Int,
    val cateId: Int,
    val type: TransactionType,
    val title: String,
    val money: Float,
    val unit: String,
    val date: String,
    val note: String? = null,
)
