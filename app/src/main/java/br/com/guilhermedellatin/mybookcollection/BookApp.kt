package br.com.guilhermedellatin.mybookcollection

import android.app.Application
import br.com.guilhermedellatin.mybookcollection.di.androidModule
import org.koin.android.ext.android.startKoin
import org.koin.standalone.StandAloneContext.stopKoin

//Inicia o grafo de dependencias do Koin
class BookApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(androidModule))
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}