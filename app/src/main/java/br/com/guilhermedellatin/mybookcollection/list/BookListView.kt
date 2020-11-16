package br.com.guilhermedellatin.mybookcollection.list

import br.com.guilhermedellatin.mybookcollection.model.Book

interface BookListView {
    fun showBooks(books: List<Book>)
    fun showBookDetails(book: Book)
    fun showDeleteMode()
    fun hideDeleteMode()
    fun showSelectedBooks(books: List<Book>)
    fun updateSelectionCountText(count: Int)
    fun showMessageHotelsDeleted(count: Int)
}