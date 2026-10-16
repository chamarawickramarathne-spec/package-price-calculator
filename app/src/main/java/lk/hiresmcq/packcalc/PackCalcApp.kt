package lk.hiresmcq.packcalc

import android.app.Application
import lk.hiresmcq.packcalc.di.AppContainer
import lk.hiresmcq.packcalc.domain.UsageEvent

class PackCalcApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.usageLogRepository.log(UsageEvent.AppOpen)
    }
}
