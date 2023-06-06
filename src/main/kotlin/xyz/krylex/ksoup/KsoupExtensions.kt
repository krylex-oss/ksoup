package xyz.krylex.ksoup

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import xyz.krylex.ksoup.exception.BadContentTypeException
import xyz.krylex.ksoup.plugin.Ksoup

internal typealias Parsers = Map<ContentType, Parser>

private fun ContentType?.matchParser(parsers: Parsers): Parser? = parsers[this]

/**
 * An extension function for [HttpResponse] to get a parsed [Document] instance or null.
 * If the content type of the response does not match any of
 * the registered parsers, it returns null.
 *
 * @receiver [HttpResponse]
 *
 * @param parsers optional, the [Parsers] to use for parsing response
 *
 * @return parsed document or null
 */
suspend fun HttpResponse.documentOrNull(parsers: Parsers = emptyMap()): Document? =
    //newInstance initializing a new TreeBuilder, that
    //allows independent (multi-threaded) use.
    contentType().matchParser(parsers)?.newInstance()
        ?.parseInput(bodyAsText(), request.url.toString())

/**
 * An extension function for [HttpResponse] to get a parsed [Document] instance.
 * If the content type of the response does not match any of
 * the registered parsers, it throws a [BadContentTypeException].
 *
 * @receiver [HttpResponse]
 *
 * @param parsers optional, the [Parsers] to use for parsing response
 *
 * @return parsed document
 *
 * @throws BadContentTypeException if the content type of the response
 * does not match any of the registered parsers.
 */
suspend fun HttpResponse.document(parsers: Parsers = emptyMap()): Document =
    documentOrNull(parsers) ?: throw BadContentTypeException(contentType())

/**
 * An extension function for [HttpResponse] to get a parsed [Document] instance or null.
 * If the content type of the response does not match any of
 * the registered parsers, it returns null.
 *
 * @receiver [HttpClient]
 *
 * @param url the url to get document from
 * @param parsers optional, the [Parsers] to use for parsing response
 * @param requestBuilder a builder function to configure HTTP request
 *
 * @return parsed document or null
 */
suspend fun HttpClient.getDocumentOrNull(
    url: Url,
    parsers: Parsers = mapOf(),
    requestBuilder: HttpRequestBuilder.() -> Unit = {}
): Document? = get(url, requestBuilder).documentOrNull(parsers)

/**
 * An extension function for [HttpClient] to get a parsed [Document] instance.
 * If the content type of the response does not match any of
 * the registered parsers, it throws a [BadContentTypeException].
 *
 * @receiver [HttpClient]
 *
 * @param url the url to fetch document from
 * @param parsers optional, the [Parsers] to use for parsing the response
 * @param requestBuilder a builder function to configure HTTP request
 *
 * @return parsed document
 *
 * @throws BadContentTypeException if the content type of the response
 * does not match any of the registered parsers.
 */
suspend fun HttpClient.getDocument(
    url: Url,
    parsers: Parsers = emptyMap(),
    requestBuilder: HttpRequestBuilder.() -> Unit = {}
): Document = get(url, requestBuilder).document(parsers)

/**
 * An extension function for [HttpClientConfig] used to install
 * an instance of the Ksoup plugin with a custom configuration.
 *
 * Usage:
 * ```
 * val client = HttpClient(CIO) {
 *     ksoup()
 *     // or
 *     ksoup {
 *     // configuration
 *     }
 * }
 * ```
 *
 * @receiver [HttpClientConfig]
 *
 * @param block a builder function to configure this instance of [Ksoup]
 */
fun HttpClientConfig<*>.ksoup(block: Ksoup.Config.() -> Unit = {}) =
    install(Ksoup, block)