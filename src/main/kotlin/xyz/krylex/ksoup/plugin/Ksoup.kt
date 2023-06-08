package xyz.krylex.ksoup.plugin

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import xyz.krylex.ksoup.Parsers
import xyz.krylex.ksoup.document
import xyz.krylex.ksoup.documentOrNull

/**
 * A Ktor client plugin that provides support to
 * parse response bodies into [Document].
 *
 * Sample usage:
 * ```
 * val client = HttpClient(CIO) {
 *     install(Ksoup)
 * }
 * val document: Document = client.get("https://example.com/doc.html").body()
 * ```
 *
 * By default, parses only [ContentType.Text.Html], [ContentType.Text.Xml],
 * [ContentType.Application.Xml] and [ContentType.Application.Rss].
 *
 * It is possible to register additional content types with [Ksoup.Config.parser]:
 *
 * ```
 * val client = HttpClient(CIO) {
 *     install(Ksoup) {
 *         parser(ContentType.Text.Plaint, Parsers.htmlParser())
 *     }
 * }
 * ```
 */
class Ksoup private constructor(private val config: Config) {
    /**
     * A [Ksoup] configuration that is used during installation.
     */
    class Config {
        private val parsers: MutableMap<ContentType, Parser> = hashMapOf(
            ContentType.Text.Html to Parser.htmlParser(),
            ContentType.Text.Xml to Parser.xmlParser(),
            ContentType.Application.Xml to Parser.xmlParser(),
            ContentType.Application.Rss to Parser.xmlParser()
        )

        /**
         * Register a [Parser] for the specified [ContentType].
         *
         * @param contentType the content type to register
         * @param parser the instance of [Parser] to be registered
         */
        fun parser(contentType: ContentType, parser: Parser) {
            parsers[contentType] = parser
        }

        /**
         * Register a [Parser] for the specified [ContentType].
         *
         * @param contentType the content type to register
         * @param block a lambda function that creates the instance
         * of [Parser]
         */
        fun parser(contentType: ContentType, block: () -> Parser) {
            parsers[contentType] = block()
        }

        /**
         * Get a [Parsers] containing all registered parsers for content types.
         *
         * Default included parsers:
         * - [ContentType.Text.Html]
         * - [ContentType.Text.Xml]
         * - [ContentType.Application.Xml]
         * - [ContentType.Application.Rss]
         *
         * @return immutable [Parsers] with all registered parsers for content types.
         */
        fun parsers(): Parsers = parsers.toMap()
    }

    /**
     * A companion object used to install a plugin.
     */
    @KtorDsl
    companion object KsoupPlugin : HttpClientPlugin<Config, Ksoup> {
        override val key: AttributeKey<Ksoup> = AttributeKey("Ksoup")

        override fun prepare(block: Config.() -> Unit): Ksoup =
            Ksoup(Config().apply(block))

        override fun install(plugin: Ksoup, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (typeInfo, response) ->
                if (response !is ByteReadChannel || typeInfo.type != Document::class) {
                    proceed()
                    return@intercept
                }

                val parsers = plugin.config.parsers()
                val httpResponse = context.response

                val kType = typeInfo.kotlinType
                val body = if (kType != null && kType.isMarkedNullable) {
                    httpResponse.documentOrNull(parsers) ?: NullBody
                } else {
                    httpResponse.document(parsers)
                }

                proceedWith(HttpResponseContainer(typeInfo, body))
            }
        }
    }
}