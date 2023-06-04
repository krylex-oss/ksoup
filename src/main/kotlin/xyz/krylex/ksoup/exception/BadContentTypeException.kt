package xyz.krylex.ksoup.exception

import io.ktor.http.*

class BadContentTypeException internal constructor(contentType: ContentType?) :
    Exception("Content type \"$contentType\" cannot be parsed by jsoup.")