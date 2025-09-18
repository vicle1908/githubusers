
import android.net.Uri
import com.example.githubusers.core.search.domain.SearchFilter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val searchJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

/**
 * Helper for launching the dedicated search experience via deep links.
 */
fun openSearch(
    navigateToDeepLink: (String) -> Unit,
    query: String? = null,
    origin: String? = null,
    filter: SearchFilter? = null
) {
    val builder = Uri.parse("app://search").buildUpon()
    query?.takeIf { it.isNotBlank() }?.let { builder.appendQueryParameter("q", it) }
    origin?.takeIf { it.isNotBlank() }?.let { builder.appendQueryParameter("origin", it) }
    filter?.let { builder.appendQueryParameter("filter", searchJson.encodeToString(it)) }
    navigateToDeepLink(builder.build().toString())
}

/**
 * Decode a SearchFilter from a deep link parameter if present.
 */
fun decodeSearchFilter(raw: String?): SearchFilter? = if (raw.isNullOrBlank()) {
    null
} else {
    runCatching {
        searchJson.decodeFromString(SearchFilter.serializer(), raw)
    }.getOrNull()
}
