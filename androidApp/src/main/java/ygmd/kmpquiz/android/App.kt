package ygmd.kmpquiz.android

import android.app.Application
import koin.initKoin
import ygmd.kmpquiz.android.koin.androidModule

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        initKoin(androidModule)
    }
}