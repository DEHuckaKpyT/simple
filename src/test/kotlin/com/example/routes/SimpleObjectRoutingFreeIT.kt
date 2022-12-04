package com.example.routes

import com.example.model.SimpleObject
import com.example.model.SimpleObjects
import com.example.service.SimpleObjectService
import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcTestContainerExtension
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

/**
 * Created on 01.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class SimpleObjectRoutingFreeIT : FreeSpec({
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

    "nested level 1" - {
        "nested1 level 2" - {
            lateinit var actual: List<SimpleObject>

            "arrange" {
                simpleObjectService.create("name1", "value1")
            }

            "act" {
                actual = simpleObjectService.getAll()
            }

            "assert" {
                assertSoftly {
                    actual.size shouldBe 1
                    actual[0].name shouldBe "name1"
                    actual[0].value shouldBe "value1"
                }
            }
        }

        "nested2 level 2" - {
            "web test execution" {
                testApplication {
                    val response = client.get("/simple-object/")

                    assertSoftly(response) {
                        it.status shouldBe HttpStatusCode.OK
                        it.bodyAsText() shouldBe "Hello, world!"
                    }
                }
            }
        }
    }
})