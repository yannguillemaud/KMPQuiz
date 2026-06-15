package ygmd.kmpquiz.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ygmd.kmpquiz.di.initKoin

class QuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@QuizApplication)
        }
    }
}