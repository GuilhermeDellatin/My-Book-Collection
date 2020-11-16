package br.com.guilhermedellatin.mybookcollection.details

import br.com.guilhermedellatin.mybookcollection.model.Book

interface BookDetailsView {
    fun showBookDetails(book: Book)
    fun errorBookNotFound()
}