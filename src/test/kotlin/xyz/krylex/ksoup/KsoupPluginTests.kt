package xyz.krylex.ksoup

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import xyz.krylex.ksoup.exception.BadContentTypeException
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KsoupPluginTests {
    private fun resourceAsByteChannel(resource: String): ByteReadChannel =
        javaClass.getResourceAsStream("/$resource")?.toByteReadChannel()
            ?: throw NullPointerException("Test resource file $resource is missing.")

    private fun constructUrl(contentType: ContentType): Url =
        URLBuilder("localhost").apply {
            parameters.append("contentType", contentType)
        }.build()

    private fun MockRequestHandleScope.respond(
        content: ByteReadChannel, contentType: ContentType
    ) = respond(content, headers = headers {
        append(HttpHeaders.ContentType, contentType)
    })

    private val mockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val contentType = request.url.parameters["contentType"]?.let {
                    ContentType.parse(it)
                }

                val content = when (contentType) {
                    ContentType.Text.Html, ContentType.Text.Plain -> "sample.html"
                    ContentType.Text.Xml -> "sample.xml"
                    ContentType.Application.Rss -> "sample-rss.xml"
                    else -> error("Incorrect content type: $contentType.")
                }

                respond(resourceAsByteChannel(content), contentType)
            }
        }
    }

    private fun HttpClient.default(): HttpClient = config { ksoup() }

    private fun requestSampleDocument(
        contentType: ContentType, block: Document.() -> Unit = {}
    ) {
        val client = mockClient.default()
        runBlocking {
            val document = client.getDocument(constructUrl(contentType))
            block(document)
        }
    }

    @Test
    fun `test html parsing by default`() {
        requestSampleDocument(ContentType.Text.Html) {
            assertEquals("Hello from html", head().text())
        }
    }

    @Test
    fun `test xml parsing by default`() {
        requestSampleDocument(ContentType.Text.Xml) {
            assertEquals("Hello from xml", text())
        }
    }

    @Test
    fun `test rss parsing by default`() {
        requestSampleDocument(ContentType.Application.Rss) {
            assertEquals(
                "Hello from rss",
                select("rss>channel>item>description").text()
            )
        }
    }

    @Test
    fun `test should throw on missing content type with getDocument`() {
        assertThrows<BadContentTypeException> {
            requestSampleDocument(ContentType.Text.Plain)
        }
    }

    @Test
    fun `test should return null on missing content type with getDocumentOrNull`() {
        val client = mockClient.default()
        assertDoesNotThrow {
            runBlocking {
                assertNull(client.getDocumentOrNull(constructUrl(ContentType.Text.Plain)))
            }
        }
    }

    @Test
    fun `test configured content type should parse document`() {
        val client = mockClient.config {
            ksoup {
                parser(ContentType.Text.Plain, Parser.htmlParser())
            }
        }
        runBlocking {
            val document: Document = client.get(constructUrl(ContentType.Text.Plain))
                .body()
            assertEquals("Hello from html", document.head().text())
        }
    }
}