package br.com.guilhermedellatin.mybookcollection.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.form.BookFormFragment
import br.com.guilhermedellatin.mybookcollection.list.BookListFragment

class BookActivity : AppCompatActivity(),
    BookListFragment.OnBookClickListener,
    BookListFragment.OnBookDeletedListener,
    SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener,
    BookFormFragment.OnBookSavedListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
    }
}