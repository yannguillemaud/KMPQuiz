package ygmd.kmpquiz.android.koin

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ygmd.kmpquiz.android.notification.AndroidNotificationScheduler

val androidModule = module {
    single {
        AndroidNotificationScheduler(context = androidContext())
    }
}