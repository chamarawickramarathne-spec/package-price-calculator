package lk.hiresmcq.packcalc.data.repo

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    Result.failure(RemoteException(httpMessage(e), e.code(), e))
} catch (e: IOException) {
    Result.failure(RemoteException("Could not reach the server. Check your internet connection.", null, e))
} catch (e: Exception) {
    Result.failure(RemoteException(e.message ?: "Unexpected error occurred.", null, e))
}

class RemoteException(
    override val message: String,
    val httpCode: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

private fun httpMessage(e: HttpException): String = when (e.code()) {
    401, 403 -> "Access denied by the server (check your Supabase key or policies)."
    404 -> "The requested record was not found."
    409 -> "This record conflicts with existing data (duplicate value)."
    in 500..599 -> "Server error (${e.code()}). Please try again later."
    else -> "Request failed (${e.code()}). ${e.message().orEmpty()}".trim()
}
