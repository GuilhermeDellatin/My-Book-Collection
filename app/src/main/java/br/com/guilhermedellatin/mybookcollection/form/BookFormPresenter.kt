package br.com.guilhermedellatin.mybookcollection.form

import br.com.guilhermedellatin.mybookcollection.model.Book
import br.com.guilhermedellatin.mybookcollection.repository.BookRepository

class BookFormPresenter(
    private val view: BookFormView,
    private val repository: BookRepository
) {
    private val validator = BookValidator()

    fun loadBook(id: Long){
        repository.bookById(id){ book ->
            if (book != null){
                view.showBook(book)
            }
        }
    }

    fun saveBook(book: Book): Boolean {
        return if (validator.validate(book)){
            try {
                repository.save(book)
                true
            } catch (e: Exception){
                view.errorSaveBook()
                false
            }
        } else {
            view.errorInvalidBook()
            false
        }
    }
}
