package com.example.plugins

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration


/**
 * Created on 29.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(300)
        timeout = Duration.ofSeconds(300)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = JacksonWebsocketContentConverter()
    }
}
