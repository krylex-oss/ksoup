package xyz.krylex.ksoup.exception

import io.ktor.http.*

/**
 * Exception is thrown when a content type does not have a parser.
 */
class BadContentTypeException internal constructor(contentType: ContentType?) :
    Exception("Content type \"$contentType\" cannot be parsed by jsoup.")