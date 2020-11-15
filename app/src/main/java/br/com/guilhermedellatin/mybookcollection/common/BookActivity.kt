package br.com.guilhermedellatin.mybookcollection.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.guilhermedellatin.mybookcollection.R

class BookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
    }
}