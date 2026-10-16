package lk.hiresmcq.packcalc.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = false
        coerceInputValues = true
    }

    fun createApi(baseUrl: String, apiKey: String, debugLogging: Boolean): SupabaseApi {
        val normalizedBase = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (debugLogging) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            clientBuilder.addInterceptor(logging)
        }

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizedBase)
            .client(clientBuilder.build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        return retrofit.create(SupabaseApi::class.java)
    }

    fun createGitHubApi(debugLogging: Boolean): GitHubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(buildUpdateClient(debugLogging))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(GitHubApi::class.java)
    }

    fun createUpdateClient(debugLogging: Boolean): OkHttpClient = buildUpdateClient(debugLogging)

    private fun buildUpdateClient(debugLogging: Boolean): OkHttpClient {
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
