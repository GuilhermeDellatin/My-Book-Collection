package br.com.guilhermedellatin.mybookcollection.form

import br.com.guilhermedellatin.mybookcollection.model.Book

interface BookFormView {
    fun showBook(book: Book)
    fun errorInvalidBook()
    fun errorSaveBook()
}