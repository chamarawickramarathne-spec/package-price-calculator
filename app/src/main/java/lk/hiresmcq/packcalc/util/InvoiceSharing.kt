package lk.hiresmcq.packcalc.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Shares a generated invoice PDF and copies it into a user-chosen location. */
object InvoiceSharing {

    private const val MIME_PDF = "application/pdf"

    fun shareIntent(context: Context, file: File): Intent {
        val uri = uriFor(context, file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = MIME_PDF
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, "Share invoice")
    }

    suspend fun copyTo(context: Context, file: File, destination: Uri) = withContext(Dispatchers.IO) {
        val output = context.contentResolver.openOutputStream(destination)
            ?: error("Could not open the selected location")
        output.use { target -> file.inputStream().use { source -> source.copyTo(target) } }
    }

    private fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
