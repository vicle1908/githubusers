package com.example.githubusers.core.search

/**
 * Normalises a raw search query string into structured qualifiers and free-text terms.
 */
object SearchQueryNormalizer {

    fun normalize(raw: String): NormalizedSearchQuery {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            return NormalizedSearchQuery(original = "", qualifiers = emptyList(), terms = emptyList())
        }

        val qualifiers = mutableListOf<NormalizedSearchQuery.Qualifier>()
        val terms = mutableListOf<String>()

        trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { token ->
            val delimiterIndex = token.indexOf(':')
            if (delimiterIndex > 0 && delimiterIndex < token.lastIndex) {
                val key = token.substring(0, delimiterIndex).lowercase()
                val value = token.substring(delimiterIndex + 1).trim()
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    qualifiers += NormalizedSearchQuery.Qualifier(key = key, value = value)
                } else {
                    terms += token
                }
            } else {
                terms += token
            }
        }

        return NormalizedSearchQuery(
            original = trimmed,
            qualifiers = qualifiers,
            terms = terms.map { it.lowercase() }
        )
    }

    data class NormalizedSearchQuery(val original: String, val qualifiers: List<Qualifier>, val terms: List<String>) {
        data class Qualifier(val key: String, val value: String)

        fun firstQualifier(key: String): String? = qualifiers.firstOrNull { it.key == key.lowercase() }?.value

        fun joinedTerms(delimiter: String = " "): String? = terms.takeIf {
            it.isNotEmpty()
        }?.joinToString(separator = delimiter)
    }
}
