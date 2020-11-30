package br.com.guilhermedellatin.mybookcollection.list

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.ListFragment
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.model.Book
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BookListFragment : ListFragment(),
    BookListView,
    AdapterView.OnItemLongClickListener,
    ActionMode.Callback {

    private val presenter: BookListPresenter by inject { parametersOf(this) }
    private var actionMode: ActionMode? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
        presenter.init()
        listView.onItemLongClickListener = this
    }

    override fun showBooks(books: List<Book>) {
        val adapter = BookAdapter(requireContext(), books)
        listAdapter = adapter
    }

    override fun showBookDetails(book: Book) {
        if (activity is OnBookClickListener) {
            val listener = activity as OnBookClickListener
            listener.onBookClick(book)
        }
    }

    override fun showMessageBooksDeleted(count: Int) {
        Snackbar.make(listView, getString(R.string.message_book_deleted, count), Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                presenter.undoDelete()
            }
            .show()
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val book = l?.getItemAtPosition(position) as Book
        presenter.selectBook(book)
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val consumed = (actionMode == null)
        if (consumed){
            val book = parent?.getItemAtPosition(position) as Book
            presenter.showDeleteMode()
            presenter.selectBook(book)
        }
        return consumed
    }

    override fun showDeleteMode() {
        val appCompatActivity = (activity as AppCompatActivity)
        actionMode = appCompatActivity.startSupportActionMode(this)
        listView.onItemLongClickListener = null
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    override fun hideDeleteMode() {
        listView.onItemLongClickListener = this
        for (i in 0 until listView.count){
            listView.setItemChecked(i, false)
        }
        listView.post {
            actionMode?.finish()
            listView.choiceMode = ListView.CHOICE_MODE_NONE
        }
    }

    override fun updateSelectionCountText(count: Int) {
        view?.post {
            actionMode?.title = resources.getQuantityString(R.plurals.list_book_selected, count, count)
        }
    }

    override fun showSelectedBooks(books: List<Book>) {
        listView.post {
            for (i in 0 until listView.count) {
                val book = listView.getItemAtPosition(i) as Book
                if (books.find { it.id == book.id } != null) {
                    listView.setItemChecked(i ,true)
                }
            }
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete) {
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            };

            builder?.setTitle(R.string.warring_title)
            builder?.setMessage(R.string.delete_message)
            builder?.setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        presenter.deleteSelected { hotels ->
                            if (activity is OnBookDeletedListener) {
                                (activity as OnBookDeletedListener).onBooksDeleted(hotels)
                            }
                        }
                    })
            builder?.setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            val dialog: AlertDialog? = builder?.create()
            dialog?.show()

            return true
        }
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        activity?.menuInflater?.inflate(R.menu.book_delete_list, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        presenter.hideDeleteMode()
    }

    interface OnBookClickListener {
        fun onBookClick(book: Book)
    }

    interface OnBookDeletedListener {
        fun onBooksDeleted(books: List<Book>)
    }

    fun search(text: String){
        presenter.searchBooks(text)
    }

    fun clearSearch(){
        presenter.searchBooks("")
    }
}