package lk.hiresmcq.packcalc.di

import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.remote.ApkDownloader
import lk.hiresmcq.packcalc.data.remote.GitHubApi
import lk.hiresmcq.packcalc.data.remote.NetworkModule
import lk.hiresmcq.packcalc.data.remote.UpdateNetwork
import lk.hiresmcq.packcalc.data.repo.UpdateRepository

/**
 * Self-update wiring for the **github** distribution channel: this flavor
 * ships the in-app updater, so the GitHub Releases service is created here.
 *
 * The created service is kept in a typed field on purpose. R8 full mode sees
 * no subtypes of a Retrofit service interface (the service is a dynamic
 * proxy), so a `create()` result that is not used anywhere is treated as
 * null and the following `check-cast` can never succeed - R8 then replaces
 * the cast with an unconditional `throw ClassCastException()`.
 */
object SelfUpdateWiring {

    private val githubApi: GitHubApi =
        UpdateNetwork.createGitHubApi(NetworkModule.json, debugLogging = BuildConfig.DEBUG)

    val repository: UpdateRepository? = UpdateRepository(githubApi)

    val downloader: ApkDownloader? =
        ApkDownloader(UpdateNetwork.createUpdateClient(debugLogging = BuildConfig.DEBUG))
}
