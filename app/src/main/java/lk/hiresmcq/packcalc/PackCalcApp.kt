package lk.hiresmcq.packcalc

import android.app.Application
import lk.hiresmcq.packcalc.di.AppContainer

class PackCalcApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
