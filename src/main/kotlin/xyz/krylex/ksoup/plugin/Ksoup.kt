package xyz.krylex.ksoup.plugin

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import xyz.krylex.ksoup.Parsers
import xyz.krylex.ksoup.document

class Ksoup private constructor(private val config: Config) {
    class Config {
        private val parsers: MutableMap<ContentType, Parser> = hashMapOf()

        fun parser(contentType: ContentType, parser: Parser) {
            parsers[contentType] = parser
        }

        fun parser(contentType: ContentType, block: () -> Parser) {
            parsers[contentType] = block()
        }

        fun parsers(): Parsers = parsers.toMap()
    }

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

                val document = context.response.document(plugin.config.parsers())
                proceedWith(HttpResponseContainer(typeInfo, document))
            }
        }
    }
}