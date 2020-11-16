package br.com.guilhermedellatin.mybookcollection.common

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.details.BookDetailsActivity
import br.com.guilhermedellatin.mybookcollection.details.BookDetailsFragment
import br.com.guilhermedellatin.mybookcollection.form.BookFormFragment
import br.com.guilhermedellatin.mybookcollection.list.BookListFragment
import br.com.guilhermedellatin.mybookcollection.model.Book
import kotlinx.android.synthetic.main.activity_book.*

class BookActivity : AppCompatActivity(),
    BookListFragment.OnBookClickListener,
    BookListFragment.OnBookDeletedListener,
    SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener,
    BookFormFragment.OnBookSavedListener {

    private var bookIdSelected: Long = -1
    private var lastSearchTerm: String = ""
    private var searchView: SearchView? = null

    private val listFragment: BookListFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentList) as BookListFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        fabAdd.setOnClickListener {
            listFragment.hideDeleteMode()
            BookFormFragment.newInstance().open(supportFragmentManager)
        }
    }

    override fun onStart() {
        super.onStart()
        setPermission();
    }

    private fun setPermission() {
        if (!getPermission()) {
            val permissionsList = listOf<String>(android.Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissionsList.toTypedArray(), 1)
        }
    }

    private fun getPermission(): Boolean{
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putLong(EXTRA_BOOK_ID_SELECTED, bookIdSelected)
        outState?.putString(EXTRA_SEARCH_TERM, lastSearchTerm)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
       bookIdSelected = savedInstanceState?.getLong(EXTRA_BOOK_ID_SELECTED) ?: 0
        lastSearchTerm = savedInstanceState?.getString(EXTRA_SEARCH_TERM) ?: ""
    }

    override fun onBookClick(book: Book) {
        if (isTablet()) {
            bookIdSelected = book.id
            showDetailsFragment(book.id)
        } else {
            showDetailsActivity(book.id)
        }
    }

    private fun showDetailsActivity(bookId: Long) {
        BookDetailsActivity.open(this, bookId)
    }

    private fun showDetailsFragment(bookId: Long) {
        searchView?.setOnQueryTextListener(null)
        val fragment = BookDetailsFragment.newInstance(bookId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.details, fragment, BookDetailsFragment.TAG_DETAILS).commit()
    }

    private fun isTablet() = resources.getBoolean(R.bool.tablet)
    private fun isSmarthphone() = resources.getBoolean(R.bool.smarthphone)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchItem?.setOnActionExpandListener(this)
        searchView = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.hint_search)
        searchView?.setOnQueryTextListener(this)

        if (lastSearchTerm.isNotEmpty()) {
            Handler().post {
                val query = lastSearchTerm
                searchItem.expandActionView()
                searchView?.setQuery(query, true)
                searchView?.clearFocus()
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_info ->
                AboutDialogFragment().show(supportFragmentManager, "sobre")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        lastSearchTerm = newText ?: ""
        listFragment.search(lastSearchTerm)
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem?) = true

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        lastSearchTerm = ""
        listFragment.clearSearch()
        return true
    }

    companion object {
        const val EXTRA_SEARCH_TERM = "lastSearch"
        const val EXTRA_BOOK_ID_SELECTED = "lastSelectedId"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            listFragment.search(lastSearchTerm)
        }
    }

    override fun onBookSaved(book: Book) {
        listFragment.search(lastSearchTerm)
        val detailsFragment =
            supportFragmentManager.findFragmentByTag(BookDetailsFragment.TAG_DETAILS) as? BookDetailsFragment
        if (detailsFragment != null && book.id == bookIdSelected) {
            showDetailsFragment(bookIdSelected)
        }
    }

    override fun onBooksDeleted(books: List<Book>) {
        if (books.find { it.id == bookIdSelected } != null) {
            val fragment =
                supportFragmentManager.findFragmentByTag(BookDetailsFragment.TAG_DETAILS)
            if (fragment != null) {
                supportFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
    }

}