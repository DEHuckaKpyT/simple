package com.example.routes

import com.example.model.SimpleObjects
import com.example.service.SimpleObjectService
import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcTestContainerExtension
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer


/**
 * Created on 04.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class SimpleObjectWebSocketsIT : StringSpec({

    val simpleObjectService = SimpleObjectService()
    val postgres = PostgreSQLContainer<Nothing>("postgres:14.5").apply {
        startupAttempts = 1
    }
    val ds = install(JdbcTestContainerExtension(postgres)) {
        maximumPoolSize = 8
        idleTimeout = 10000
    }
    Database.connect(ds)
    transaction {
        SchemaUtils.create(
            SimpleObjects
        )
    }

    "Отправка сообщений" {
        testApplication {
            val client = createClient {
                install(WebSockets)
            }
            client.webSocket("/simple-object/1/operator") {
                val operatorAdded = (incoming.receive() as? Frame.Text)?.readText()

                send(Frame.Text("JetBrains"))
                val responseText = (incoming.receive() as Frame.Text).readText()

                assertSoftly {
                    operatorAdded shouldBe "operator added"

                }
            }

        }
    }


})