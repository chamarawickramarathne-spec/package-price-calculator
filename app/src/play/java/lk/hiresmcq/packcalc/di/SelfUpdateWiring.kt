package lk.hiresmcq.packcalc.di

import lk.hiresmcq.packcalc.data.remote.ApkDownloader
import lk.hiresmcq.packcalc.data.repo.UpdateRepository

/**
 * Self-update wiring for the **play** distribution channel: there is no
 * in-app updater, because Google Play delivers the updates and the app must
 * not declare REQUEST_INSTALL_PACKAGES.
 *
 * The GitHub Releases service is deliberately **not** created here. Building
 * it in this flavor made R8 replace the `check-cast` to the service interface
 * with an unconditional `throw ClassCastException()` (in R8 full mode a
 * Retrofit service has no subtypes - it is a dynamic proxy - so an unused
 * `create()` result is treated as null and the cast can never succeed).
 * That killed every Play launch inside `Application.onCreate` in v1.6.4.
 */
object SelfUpdateWiring {

    val repository: UpdateRepository? = null

    val downloader: ApkDownloader? = null
}
