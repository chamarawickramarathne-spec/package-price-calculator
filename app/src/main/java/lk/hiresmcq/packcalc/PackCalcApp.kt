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
        // Retry an email that never reached user_emails (saved while offline).
        container.userEmailRepository.syncPending()
        // Backfill installs that signed in before business details moved to the
        // login gate (v1.6.0): the remembered email is the PDF contact email.
        container.backfillBusinessEmail()
    }
}
