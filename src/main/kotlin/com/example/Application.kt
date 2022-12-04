package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*

fun main(args: Array<String>): Unit =
    io.ktor.server.tomcat.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureWebSockets()
    configureSerialization()
    configureRouting()
    configureDatabase()
}
