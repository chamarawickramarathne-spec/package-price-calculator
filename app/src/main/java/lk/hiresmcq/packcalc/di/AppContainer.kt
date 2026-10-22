package lk.hiresmcq.packcalc.di

import android.content.Context
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.local.AppDatabase
import lk.hiresmcq.packcalc.data.remote.ApkDownloader
import lk.hiresmcq.packcalc.data.remote.NetworkModule
import lk.hiresmcq.packcalc.data.remote.SupabaseApi
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.LocalPackageRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.data.repo.UpdateRepository
import lk.hiresmcq.packcalc.data.repo.UsageLogRepository
import lk.hiresmcq.packcalc.data.repo.UserEmailRepository
import lk.hiresmcq.packcalc.util.BusinessStore
import lk.hiresmcq.packcalc.util.SessionStore

class AppContainer(context: Context) {

    private val api: SupabaseApi = NetworkModule.createApi(
        baseUrl = BuildConfig.SUPABASE_URL + "/rest/v1/",
        apiKey = BuildConfig.SUPABASE_ANON_KEY,
        debugLogging = BuildConfig.DEBUG,
    )

    private val database: AppDatabase = AppDatabase.build(context)
    private val appContext = context.applicationContext

    val sessionStore = SessionStore(appContext)
    val businessStore = BusinessStore(appContext)
    val catalogRepository = CatalogRepository(api)
    val templateRepository = TemplateRepository(api)
    val packageRepository = LocalPackageRepository(database)
    val usageLogRepository = UsageLogRepository(api, appContext)
    val userEmailRepository = UserEmailRepository(api, sessionStore, appContext)

    /**
     * The in-app updater exists only in the github distribution channel. The
     * play flavor wires no updater (see SelfUpdateWiring in the flavor source
     * sets), so it never builds the GitHub Releases service, makes no GitHub
     * API calls and never needs REQUEST_INSTALL_PACKAGES - Google Play
     * delivers updates for that channel instead.
     */
    val updateRepository: UpdateRepository? = SelfUpdateWiring.repository
    val apkDownloader: ApkDownloader? = SelfUpdateWiring.downloader

    /**
     * One-time local migration for installs that completed the email gate
     * before business details moved there (v1.6.0): the remembered sign-in
     * email becomes the profile's contact email so quotes keep their line.
     */
    fun backfillBusinessEmail() {
        val email = sessionStore.email ?: return
        val profile = businessStore.load()
        if (profile.email.isBlank()) {
            businessStore.save(profile.copy(email = email))
        }
    }
}
