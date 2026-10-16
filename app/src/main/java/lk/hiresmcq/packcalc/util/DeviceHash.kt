package lk.hiresmcq.packcalc.util

import java.security.MessageDigest

/**
 * Turns a raw identifier into a short, stable, non-reversible token.
 *
 * Pure JVM (no Android imports) so it is unit-testable. The result is the first
 * 32 hex characters of the SHA-256 digest of [raw] - enough to tell devices
 * apart, but not enough to recover the original identifier.
 */
object DeviceHash {

    private const val LENGTH = 32

    fun of(raw: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it.toInt() and 0xFF) }
            .take(LENGTH)
    }
}
