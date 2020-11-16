package br.com.guilhermedellatin.mybookcollection.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.form.BookFormFragment
import br.com.guilhermedellatin.mybookcollection.model.Book

class BookDetailsActivity: AppCompatActivity(), BookFormFragment.OnBookSavedListener {
    private val bookId: Long by lazy { intent.getLongExtra(EXTRA_BOOK_ID, -1)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        if (savedInstanceState == null) {
            showBookDetailsFragment()
        }
    }

    private fun showBookDetailsFragment() {
        val fragment = BookDetailsFragment.newInstance(bookId)
        supportFragmentManager.beginTransaction().replace(
            R.id.details, fragment,
            BookDetailsFragment.TAG_DETAILS
        ).commit()
    }

    override fun onBookSaved(book: Book) {
        setResult(RESULT_OK)
        showBookDetailsFragment()
    }

    companion object {
        private const val EXTRA_BOOK_ID = "book_id"
        fun open(activity: Activity, bookId: Long){
            activity.startActivityForResult(
                Intent(activity, BookDetailsActivity::class.java).apply {
                    putExtra(EXTRA_BOOK_ID, bookId)
                }, 0)
        }
    }
}