package ygmd.kmpquiz.android

import android.app.Application
import koin.initKoin

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}