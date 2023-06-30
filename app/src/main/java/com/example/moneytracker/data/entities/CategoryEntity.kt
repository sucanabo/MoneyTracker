package com.example.moneytracker.data.entities

object CategoryEntity {
    const val TABLE_NAME = "categories"
    const val COL_ID = "_id"
    const val COL_NAME = "_name"
    const val COL_IMG = "_img"
    fun buildScheme() = """
        CREATE TABLE $TABLE_NAME(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NAME TEXT,
            $COL_IMG TEXT
        )
    """.trimIndent()
    fun drop() = """DROP TABLE IF EXISTS $TABLE_NAME """.trimIndent()

}