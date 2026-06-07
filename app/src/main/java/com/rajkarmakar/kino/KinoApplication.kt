package com.rajkarmakar.kino

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KinoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
