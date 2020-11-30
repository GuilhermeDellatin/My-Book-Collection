package br.com.guilhermedellatin.mybookcollection.repository.sqlite

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import java.lang.IllegalArgumentException

class BookProvider : ContentProvider() {

    private lateinit var helper: BookSqlHelper

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val rowsDeleted = when (uriType) {
            TYPE_BOOK_DIR -> sqlDB.delete(TABLE_BOOK, selection, selectionArgs)
            TYPE_BOOK_ITEM -> {
                val id = uri.lastPathSegment
                sqlDB.delete(TABLE_BOOK, "$COLUMN_ID = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("URI não suportada: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        val uriType = sUriMatcher.match(uri)
        return when (uriType) {
            TYPE_BOOK_DIR -> return "${ContentResolver.CURSOR_DIR_BASE_TYPE}/br.com.guilhermedellatin.mybookcollection"
            TYPE_BOOK_ITEM -> return "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/br.com.guilhermedellatin.mybookcollection"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val id: Long
        when (uriType) {
            TYPE_BOOK_DIR -> id = sqlDB.insertWithOnConflict(TABLE_BOOK, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            else -> throw IllegalArgumentException("URI não suportada: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(CONTENT_URI, id.toString())
    }

    override fun onCreate(): Boolean {
        helper = BookSqlHelper(context)
        return true
    }

    override fun query(
            uri: Uri, projection: Array<String>?, selection: String?,
            selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val uriType = sUriMatcher.match(uri)
        val db = helper.writableDatabase
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = TABLE_BOOK
        val cursor: Cursor
        when (uriType) {
            TYPE_BOOK_DIR -> cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
            TYPE_BOOK_ITEM -> {
                queryBuilder.appendWhere("$COLUMN_ID = ?")
                cursor = queryBuilder.query(db, projection, selection, arrayOf(uri.lastPathSegment), null, null, null)
            }
            else -> throw IllegalArgumentException("URI não suportada: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun update(
            uri: Uri, values: ContentValues?, selection: String?,
            selectionArgs: Array<String>?
    ): Int {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val rowsAffected = when (uriType) {
            TYPE_BOOK_DIR ->
                sqlDB.update(TABLE_BOOK, values, selection, selectionArgs)
            TYPE_BOOK_ITEM -> {
                val id = uri.lastPathSegment
                sqlDB.update(TABLE_BOOK, values, "$COLUMN_ID= ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("URI não suportada: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsAffected
    }

    companion object {
        private const val AUTHORITY = "br.com.guilhermedellatin.mybookcollection"
        private const val PATH = "books"
        private const val TYPE_BOOK_DIR = 1
        private const val TYPE_BOOK_ITEM = 2
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")
        private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(AUTHORITY, PATH, TYPE_BOOK_DIR)
            sUriMatcher.addURI(AUTHORITY, "$PATH/#", TYPE_BOOK_ITEM)
        }
    }

}