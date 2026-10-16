package lk.hiresmcq.packcalc.data.remote

import lk.hiresmcq.packcalc.data.model.GitHubRelease
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun latestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): GitHubRelease
}
