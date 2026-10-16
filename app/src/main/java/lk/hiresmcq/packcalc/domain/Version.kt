package lk.hiresmcq.packcalc.domain

/**
 * A semantic version of the form major.minor.patch. Used to compare the
 * installed app version against the latest published GitHub release tag.
 */
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
) : Comparable<Version> {

    override fun compareTo(other: Version): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })

    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        /**
         * Parses strings such as "1.0.2", "v1.0.2", "1.2" or "v1.2.3-beta.1".
         * Returns null when the value cannot be interpreted as a version.
         */
        fun parse(raw: String?): Version? {
            val cleaned = raw
                ?.trim()
                ?.removePrefix("v")
                ?.substringBefore('-')
                ?.substringBefore('+')
                ?.trim()
                .orEmpty()
            if (cleaned.isEmpty()) return null

            val numbers = cleaned.split('.').map { it.toIntOrNull() ?: return null }
            return Version(
                major = numbers.getOrElse(0) { 0 },
                minor = numbers.getOrElse(1) { 0 },
                patch = numbers.getOrElse(2) { 0 },
            )
        }
    }
}
