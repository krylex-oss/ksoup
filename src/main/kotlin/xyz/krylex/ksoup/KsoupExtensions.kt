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

private fun ContentType?.matchParser(parsers: Parsers): Parser? =
    parsers[this] ?: when (this) {
        ContentType.Text.Html -> Parser.htmlParser()
        ContentType.Text.Xml, ContentType.Application.Xml -> Parser.xmlParser()
        else -> null
    }

suspend fun HttpResponse.documentOrNull(parsers: Parsers = emptyMap()): Document? =
    contentType().matchParser(parsers)?.newInstance()
        ?.parseInput(bodyAsText(), request.url.toString())

suspend fun HttpResponse.document(parsers: Parsers = emptyMap()): Document =
    documentOrNull(parsers) ?: throw BadContentTypeException(contentType())

suspend fun HttpClient.getDocumentOrNull(
    url: String, parsers: Parsers = mapOf(), requestBuilder: HttpRequestBuilder.() -> Unit
): Document? = get(url, requestBuilder).documentOrNull(parsers)

suspend fun HttpClient.getDocument(url: String, parsers: Parsers = emptyMap()) : Document =
    get(url).document(parsers)

fun HttpClientConfig<*>.ksoup(block: Ksoup.Config.() -> Unit) = install(Ksoup, block)