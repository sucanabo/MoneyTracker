package com.example.moneytracker.domain.model

import java.io.Serializable

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

fun TransactionType.getTitle(): String = when (this) {
    TransactionType.ADD -> "add money"
    TransactionType.EXPENSE -> "expense"
    else -> ""
}

data class TransactionModel(
    val id: Int = 0,
    val cateId: Int? = null,
    val type: TransactionType,
    val money: Float,
    val unit: String? = null,
    val date: String,
    val note: String? = null,
) : Serializable {
    override fun toString(): String {
        return "TransactionModel: id: $id, cateId: $cateId, type: $type, money: $money, unit: $unit, date: $date, note: $note"
    }
}
