package br.com.guilhermedellatin.mybookcollection.list

import br.com.guilhermedellatin.mybookcollection.model.Book
import br.com.guilhermedellatin.mybookcollection.repository.BookRepository

class BookListPresenter(
    private val view: BookListView,
    private val repository: BookRepository
) {
    private var lastTerm = ""
    private var inDeleteMode = false
    private var selectedItems = mutableListOf<Book>()
    private val deletedItems = mutableListOf<Book>()

    fun searchBooks(term: String) {
        lastTerm = term
        repository.search(term) { books ->
            view.showBooks(books)
        }
    }

    fun selectBook(book: Book) {
        if (inDeleteMode) {
            toggleBookSelected(book)
            if (selectedItems.size == 0) {
                view.hideDeleteMode()
            } else {
                view.updateSelectionCountText(selectedItems.size)
                view.showSelectedBooks(selectedItems)
            }
        } else {
            view.showBookDetails(book)
        }
    }

    private fun toggleBookSelected(book: Book) {
        val existing = selectedItems.find { it.id == book.id }
        if (existing == null) {
            selectedItems.add(book)
        } else {
            selectedItems.removeAll{ it.id == book.id }
        }
    }

    fun showDeleteMode() {
        inDeleteMode = true
        view.showDeleteMode()
    }

    fun hideDeleteMode() {
        inDeleteMode = false
        selectedItems.clear()
        view.hideDeleteMode()
    }

    fun refresh() {
        searchBooks(lastTerm)
    }

    fun deleteSelected(callback: (List<Book>) -> Unit) {
        repository.remove(*selectedItems.toTypedArray())
        deletedItems.clear()
        deletedItems.addAll(selectedItems)
        refresh()
        callback(selectedItems)
        hideDeleteMode()
        view.showMessageBooksDeleted(deletedItems.size)
    }

    fun init() {
        if (inDeleteMode) {
            showDeleteMode()
            view.updateSelectionCountText(selectedItems.size)
            view.showSelectedBooks(selectedItems)
        } else {
            refresh()
        }
    }

    fun undoDelete() {
        if (deletedItems.isNotEmpty()) {
            for (book in deletedItems) {
                repository.save(book)
            }
            searchBooks(lastTerm)
        }
    }
}