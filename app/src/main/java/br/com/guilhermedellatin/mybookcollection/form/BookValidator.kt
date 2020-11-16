package br.com.guilhermedellatin.mybookcollection.form

import br.com.guilhermedellatin.mybookcollection.model.Book

class BookValidator {
    fun validate(info: Book) = with(info) {
        checkName(name) && checkGender(gender) && checkPublishing(publishing)
    }

    private fun checkName(name: String) = name.length in 2..20
    private fun checkGender(gender: String) = gender.length in 2..20
    private fun checkPublishing(publishing: String) = publishing.length in 2..20
}