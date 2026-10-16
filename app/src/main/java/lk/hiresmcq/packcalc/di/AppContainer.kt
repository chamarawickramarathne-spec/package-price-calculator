package lk.hiresmcq.packcalc.di

import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.remote.NetworkModule
import lk.hiresmcq.packcalc.data.remote.SupabaseApi
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.PackageRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository

class AppContainer {

    private val api: SupabaseApi = NetworkModule.createApi(
        baseUrl = BuildConfig.SUPABASE_URL + "/rest/v1/",
        apiKey = BuildConfig.SUPABASE_ANON_KEY,
        debugLogging = BuildConfig.DEBUG,
    )

    val catalogRepository = CatalogRepository(api)
    val templateRepository = TemplateRepository(api)
    val packageRepository = PackageRepository(api)
}
