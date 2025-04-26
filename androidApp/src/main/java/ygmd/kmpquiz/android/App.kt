package ygmd.kmpquiz.android

import QuizViewModelImpl
import android.app.Application
import org.koin.dsl.module
import ygmd.kmpquiz.initKoin
import ygmd.kmpquiz.service.models.QuizViewModel

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        initKoin(module {
            single<QuizViewModel> {
                QuizViewModelImpl(get())
            }
        })
    }
}