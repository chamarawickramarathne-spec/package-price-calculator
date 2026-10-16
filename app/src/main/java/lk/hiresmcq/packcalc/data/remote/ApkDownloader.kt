package lk.hiresmcq.packcalc.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * Streams an APK from a URL to a local file, reporting progress as it goes.
 * Progress callbacks are delivered on the IO dispatcher.
 */
class ApkDownloader(private val client: OkHttpClient) {

    suspend fun download(
        url: String,
        destination: File,
        onProgress: (bytesRead: Long, total: Long) -> Unit = { _, _ -> },
    ): File = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Download failed (HTTP ${response.code})")
            }
            val body = response.body ?: throw IOException("Download failed (empty response)")
            val total = body.contentLength()
            body.byteStream().use { input ->
                destination.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var read = input.read(buffer)
                    var done = 0L
                    while (read != -1) {
                        output.write(buffer, 0, read)
                        done += read
                        onProgress(done, total)
                        read = input.read(buffer)
                    }
                }
            }
        }
        destination
    }
}
