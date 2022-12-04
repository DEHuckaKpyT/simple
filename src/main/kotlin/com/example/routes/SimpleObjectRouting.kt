package com.example.routes

import com.example.converter.SimpleObjectConverter
import com.example.routes.dto.CreateSimpleObjectDto
import com.example.routes.dto.SimpleObjectDto
import com.example.service.SimpleObjectService
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.withContext
import org.mapstruct.factory.Mappers
import java.lang.RuntimeException
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


/**
 * Created on 28.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Route.simpleObjectRouting() {

    val simpleObjectService = SimpleObjectService()
    val converter = Mappers.getMapper(SimpleObjectConverter::class.java)

    route("/simple-object") {
        get("/") {
            try {
                call.respondText("Hello, world!")
            } catch (ex: Exception) {
                call.respond(mapOf("error:" to ex.stackTraceToString()))
            }
        }

        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                call.respond(converter.toSimpleObjectDto(simpleObjectService.getById(id)))
            } catch (ex: Exception) {
                call.respond(mapOf("error:" to ex.stackTraceToString()))
            }
        }

        post("/create") {
            try {
                val createDto = call.receive<CreateSimpleObjectDto>()

                call.respond(converter.toSimpleObjectDto(simpleObjectService.create(createDto.name, createDto.value)))
            } catch (ex: Exception) {
                call.respond(mapOf("error:" to ex.stackTraceToString()))
            }
        }

        get("/list") {
            call.respond(converter.toSimpleObjectDto(simpleObjectService.getAll()))
        }

        val connectionsMap = ConcurrentHashMap<Int, DefaultWebSocketSession>()

        webSocket("/{sessionId}/{role}") {
            println("Adding connection!")
            val sessionId = call.parameters["sessionId"]!!.toInt()
            val role = call.parameters["role"]!!

            if (role == "operator") {
                connectionsMap[sessionId] = this
                try {
                    send("operator added")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        send(receivedText)
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                } finally {
                    println("Removing operator!")
                }
            }
            if (role == "user") {
                val operatorConnection = connectionsMap[sessionId]
                try {
                    send("user connected")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val simpleDto = this.converter?.deserialize<SimpleObjectDto>(frame, Charsets.UTF_8) ?: throw RuntimeException("error1")
//                        val objectDto = receiveDeserialized<SimpleObjectDto>()
                        simpleObjectService.create(simpleDto.name, simpleDto.value)

                        operatorConnection!!.send(frame)
                    }
                } catch (e: ClosedReceiveChannelException) {
                    send("closing")
                    close(CloseReason(CloseReason.Codes.NORMAL, "qwe1"))
                    println(e.stackTraceToString())
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    send("closing")
                    close(CloseReason(CloseReason.Codes.NORMAL, "qwe2"))
                    println(e.stackTraceToString())
                    println("onError ${closeReason.await()}")
                } catch (e: java.lang.Exception) {
                    send("closing")
                    close(CloseReason(CloseReason.Codes.NORMAL, "qwe3"))
                    println(e.stackTraceToString())
                } finally {
                    send("closing")
                    close(CloseReason(CloseReason.Codes.NORMAL, "qwe4"))
                    println("Removing user!")
                }
            }
        }
    }
}
