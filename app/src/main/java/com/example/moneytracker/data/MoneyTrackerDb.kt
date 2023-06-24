package com.example.moneytracker.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MoneyTrackerDb(context: Context?) : SQLiteOpenHelper(
    context,
    DbConfig.DB_NAME,
    null,
    DbConfig.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DbConfig.buildSchema())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DbConfig.dropAllTable())
        db?.execSQL(DbConfig.buildSchema())
    }
}