package com.example.moneytracker.features.transaction.data.entity

object TransactionEntity {
    const val TABLE_NAME = "transactions"
    const val COL_ID = "_id"
    const val COL_CATE_ID = "_cate_id"
    const val COL_TYPE = "_type"
    const val COL_TITLE = "_title"
    const val COL_DATE = "_date"
    const val COL_MONEY = "_money"
    const val COL_UNIT = "_unit"
    const val COL_NOTE = "_note"

    fun buildScheme() = """
            CREATE TABLE $TABLE_NAME (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_CATE_ID INTEGER,
            $COL_TYPE TEXT,
            $COL_TITLE TEXT,
            $COL_DATE TEXT,
            $COL_MONEY REAL,
            $COL_UNIT TEXT,
            $COL_NOTE TEXT
            )
        """

    fun drop() = """DROP TABLE IF EXISTS $TABLE_NAME """
}