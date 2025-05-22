package ygmd.kmpquiz.android

import android.app.Application
import org.koin.dsl.module
import ygmd.kmpquiz.initKoin

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        initKoin(module {

        })
    }
}