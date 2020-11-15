package br.com.guilhermedellatin.mybookcollection.repository

import br.com.guilhermedellatin.mybookcollection.model.Book

interface BookRepository {
    fun save(book: Book)
    fun remove(vararg books: Book)
    fun bookById(id: Long, callback: (Book?) -> Unit)
    fun search(term: String, callback: (List<Book>) -> Unit)
}