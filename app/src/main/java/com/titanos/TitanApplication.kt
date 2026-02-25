package com.titanos

import android.app.Application
import com.titanos.core.di.TitanContainer

class TitanApplication : Application() {
    lateinit var container: TitanContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = TitanContainer(this)
    }
}
