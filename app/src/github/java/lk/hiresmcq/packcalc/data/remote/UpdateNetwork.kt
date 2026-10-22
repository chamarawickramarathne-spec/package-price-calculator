package lk.hiresmcq.packcalc.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network wiring for the in-app self-updater (GitHub Releases).
 *
 * This file lives in the **github** flavor source set on purpose. The play
 * channel has no updater, and building the service there made R8 replace the
 * `check-cast` to the service interface with an unconditional
 * `throw ClassCastException()`: in R8 full mode a Retrofit service has no
 * subtypes (the service is a dynamic proxy), so an unused `create()` result
 * is treated as null and the cast can never succeed. That killed every Play
 * launch inside `Application.onCreate` (v1.6.4).
 */
object UpdateNetwork {

    fun createGitHubApi(json: Json, debugLogging: Boolean): GitHubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(createUpdateClient(debugLogging))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(GitHubApi::class.java)
    }

    fun createUpdateClient(debugLogging: Boolean): OkHttpClient {
        val headers = okhttp3.Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Accept", "application/vnd.github+json")
                    .addHeader("User-Agent", "PackagePriceCalculator-Android")
                    .build(),
            )
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(headers)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (debugLogging) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            clientBuilder.addInterceptor(logging)
        }

        return clientBuilder.build()
    }
}
