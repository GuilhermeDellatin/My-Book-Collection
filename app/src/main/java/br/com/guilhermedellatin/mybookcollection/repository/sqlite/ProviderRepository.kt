package br.com.guilhermedellatin.mybookcollection.repository.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import br.com.guilhermedellatin.mybookcollection.model.Book
import br.com.guilhermedellatin.mybookcollection.repository.BookRepository

class ProviderRepository(val ctx: Context) : BookRepository {

    override fun save(book: Book) {
        val uri = ctx.contentResolver.insert(
                BookProvider.CONTENT_URI,
                getValues(book))
        val id = uri?.lastPathSegment?.toLong() ?: -1
        if (id !=  -1L) {
            book.id = id
        }
    }

    private fun getValues(book: Book): ContentValues {
        val cv = ContentValues()
        if (book.id > 0) {
            cv.put(COLUMN_ID, book.id)
        }
        cv.put(COLUMN_NAME, book.name)
        cv.put(COLUMN_GENDER, book.gender)
        cv.put(COLUMN_PUBLISHING, book.publishing)
        cv.put(COLUMN_RATING, book.rating)
        cv.put(COLUMN_PATH, book.path)
        return cv
    }

    override fun remove(vararg books: Book) {
        books.forEach { book ->
            val uri = Uri.withAppendedPath(
                    BookProvider.CONTENT_URI, book.id.toString())
            ctx.contentResolver.delete(uri, null, null)
        }
    }

    override fun bookById(id: Long, callback: (Book?) -> Unit) {
        val cursor = ctx.contentResolver.query(
                Uri.withAppendedPath(BookProvider.CONTENT_URI, id.toString()), null, null, null, null)
        var book: Book? = null
        if (cursor?.moveToNext() == true) {
            book = bookFromCursor(cursor)
        }
        cursor?.close()
        callback(book)
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

    override fun search(term: String, callback: (List<Book>) -> Unit) {
        var where: String? = null
        var whereArgs: Array<String>? = null

        if (term.isNotEmpty()) {
            where = "$COLUMN_NAME LIKE ?"
            whereArgs = arrayOf("%$term%")
        }

        val cursor = ctx.contentResolver.query(
                BookProvider.CONTENT_URI,
                null,
                where,
                whereArgs, COLUMN_NAME)

        val books = mutableListOf<Book>()

        while (cursor?.moveToNext() == true) {
           books.add(bookFromCursor(cursor))
        }
        cursor?.close()
        callback(books)
    }

}