package lk.hiresmcq.packcalc.di

import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.remote.ApkDownloader
import lk.hiresmcq.packcalc.data.remote.NetworkModule
import lk.hiresmcq.packcalc.data.remote.SupabaseApi
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.PackageRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.data.repo.UpdateRepository

class AppContainer {

    private val api: SupabaseApi = NetworkModule.createApi(
        baseUrl = BuildConfig.SUPABASE_URL + "/rest/v1/",
        apiKey = BuildConfig.SUPABASE_ANON_KEY,
        debugLogging = BuildConfig.DEBUG,
    )

    val catalogRepository = CatalogRepository(api)
    val templateRepository = TemplateRepository(api)
    val packageRepository = PackageRepository(api)

    private val githubApi = NetworkModule.createGitHubApi(debugLogging = BuildConfig.DEBUG)
    val updateRepository = UpdateRepository(githubApi)
    val apkDownloader = ApkDownloader(NetworkModule.createUpdateClient(debugLogging = BuildConfig.DEBUG))
}
