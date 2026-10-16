package lk.hiresmcq.packcalc.data.repo

import lk.hiresmcq.packcalc.data.remote.GitHubApi
import lk.hiresmcq.packcalc.domain.Version

/** Details of a newer app release published on GitHub. */
data class UpdateInfo(
    val version: Version,
    val tagName: String,
    val downloadUrl: String,
    val htmlUrl: String?,
    val notes: String?,
    val sizeBytes: Long,
)

class UpdateRepository(private val api: GitHubApi) {

    /**
     * Returns the newest release when it is newer than [current], or null when
     * the installed version is already up to date. Fails explicitly when the
     * release exists but carries no usable APK asset.
     */
    suspend fun latestUpdate(current: Version): Result<UpdateInfo?> = safeApiCall {
        val release = api.latestRelease(REPO_OWNER, REPO_NAME)
        val latest = Version.parse(release.tagName)
        when {
            latest == null -> null
            latest <= current -> null
            else -> {
                val asset = release.assets.firstOrNull { it.name == APK_ASSET_NAME }
                    ?: release.assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
                    ?: throw RemoteException(
                        "Release ${release.tagName} has no downloadable APK asset.",
                    )
                UpdateInfo(
                    version = latest,
                    tagName = release.tagName,
                    downloadUrl = asset.browserDownloadUrl,
                    htmlUrl = release.htmlUrl,
                    notes = release.body,
                    sizeBytes = asset.size,
                )
            }
        }
    }

    companion object {
        const val REPO_OWNER = "chamarawickramarathne-spec"
        const val REPO_NAME = "package-price-calculator"
        const val APK_ASSET_NAME = "Package-Price-Calculator.apk"
    }
}
