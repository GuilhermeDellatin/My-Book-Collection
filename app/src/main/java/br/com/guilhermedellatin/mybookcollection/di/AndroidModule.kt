package br.com.guilhermedellatin.mybookcollection.di

import br.com.guilhermedellatin.mybookcollection.details.BookDetailsPresenter
import br.com.guilhermedellatin.mybookcollection.details.BookDetailsView
import br.com.guilhermedellatin.mybookcollection.form.BookFormPresenter
import br.com.guilhermedellatin.mybookcollection.form.BookFormView
import br.com.guilhermedellatin.mybookcollection.list.BookListPresenter
import br.com.guilhermedellatin.mybookcollection.list.BookListView
import br.com.guilhermedellatin.mybookcollection.repository.BookRepository
import br.com.guilhermedellatin.mybookcollection.repository.sqlite.ProviderRepository
import org.koin.dsl.module.module

val androidModule = module {
    single { this }

    single {
        ProviderRepository(ctx = get()) as BookRepository
    }

    factory { (view: BookListView) ->
        BookListPresenter(view, repository = get())
    }

    factory { (view: BookDetailsView) ->
        BookDetailsPresenter(view, repository = get())
    }

    factory { (view: BookFormView) ->
        BookFormPresenter(view, repository = get())
    }
}