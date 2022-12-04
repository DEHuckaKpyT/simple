package com.example.plugins

import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson(contentType = Json) {
        }
    }
}
