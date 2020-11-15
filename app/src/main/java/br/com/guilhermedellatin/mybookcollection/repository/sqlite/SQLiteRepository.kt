package br.com.guilhermedellatin.mybookcollection.repository.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.guilhermedellatin.mybookcollection.model.Book
import br.com.guilhermedellatin.mybookcollection.repository.BookRepository
import java.util.ArrayList

class SQLiteRepository(ctx: Context) : BookRepository {
    private val helper: BookSqlHelper = BookSqlHelper(ctx)

    private fun insert(book: Book) {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_NAME, book.name)
            put(COLUMN_GENDER, book.gender)
            put(COLUMN_PUBLISHING, book.publishing)
            put(COLUMN_RATING, book.rating)
            put(COLUMN_PATH, book.path)
        }

        val id = db.insert(TABLE_BOOK, null, cv)
        if (id != -1L) {
            book.id = id
        }

        db.close()
    }

    private fun update(book: Book) {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_ID, book.id)
            put(COLUMN_NAME, book.name)
            put(COLUMN_GENDER, book.gender)
            put(COLUMN_PUBLISHING, book.publishing)
            put(COLUMN_RATING, book.rating)
            put(COLUMN_PATH, book.path)
        }

        db.insertWithOnConflict(
                TABLE_BOOK,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
    }

    override fun save(book: Book) {
        if (book.id == 0L) {
            insert(book)
        } else {
            update(book)
        }
    }

    override fun remove(vararg books: Book) {
        val db = helper.writableDatabase
        for (book in books) {
            db.delete(
                    TABLE_BOOK,
                    "$COLUMN_ID = ?",
                    arrayOf(book.id.toString())
            )
        }
        db.close()
    }

    override fun bookById(id: Long, callback: (Book?) -> Unit) {
        val sql = "SELECT * FROM $TABLE_BOOK WHERE $COLUMN_ID = ?"
        val db = helper.readableDatabase
        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        val book = if (cursor.moveToNext()) bookFromCursor(cursor) else null
        callback(book)
    }

    override fun search(term: String, callback: (List<Book>) -> Unit) {
        var sql = "SELECT * FROM $TABLE_BOOK"
        var args: Array<String>? = null
        if (term.isNotEmpty()) {
            sql += " WHERE $COLUMN_NAME LIKE ?"
            args = arrayOf("%$term%")
        }

        sql += " ORDER BY $COLUMN_NAME"
        val db = helper.readableDatabase
        val cursor = db.rawQuery(sql, args)
        val books = ArrayList<Book>()
        while (cursor.moveToNext()) {
            val book = bookFromCursor(cursor)
            books.add(book)
        }
        cursor.close()
        db.close()
        callback(books)
    }

    private fun bookFromCursor(cursor: Cursor): Book {
        val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        val gender = cursor.getString(cursor.getColumnIndex(COLUMN_GENDER))
        val publishing = cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISHING))
        val rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING))
        val path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH))
        return Book(id, name, gender, publishing, rating, path)
    }

}