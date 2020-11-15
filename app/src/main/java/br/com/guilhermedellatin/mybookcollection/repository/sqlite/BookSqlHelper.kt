package br.com.guilhermedellatin.mybookcollection.repository.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookSqlHelper(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE $TABLE_BOOK(" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_NAME TEXT NOT NULL, " +
                        "$COLUMN_GENDER TEXT, " +
                        "$COLUMN_PUBLISHING TEXT, " +
                        "$COLUMN_RATING REAL, " +
                        "$COLUMN_PATH TEXT)"
        )
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

}