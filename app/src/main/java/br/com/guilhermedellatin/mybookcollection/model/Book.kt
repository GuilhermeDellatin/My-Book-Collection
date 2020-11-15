package br.com.guilhermedellatin.mybookcollection.model

data class Book(
        var id: Long =0,
        var name: String = "",
        var gender: String = "",
        var publishing: String = "",
        var rating: Float = 0.0F,
        var path: String = ""
) {
    override fun toString(): String = name
}