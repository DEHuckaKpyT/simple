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
class SimpleObjectRoutingFunIT : FunSpec() {

    private val simpleObjectService = SimpleObjectService()
    private val postgres = PostgreSQLContainer<Nothing>("postgres:14.5").apply {
        startupAttempts = 1
    }
    private val ds = install(JdbcTestContainerExtension(postgres)) {
        maximumPoolSize = 8
        idleTimeout = 10000
    }

    init {
        Database.connect(ds)
        transaction {
            SchemaUtils.create(
                    SimpleObjects
            )
        }

        test("simple") {
            // Arrange
            simpleObjectService.create("name1", "value1")

            // Act
            val actual = simpleObjectService.getAll()

            // Assert
            assertSoftly(actual) {
                it.size shouldBe 1
                it[0].name shouldBe "name1"
                it[0].value shouldBe "value1"
            }
        }
        test("web") {
            testApplication {
                val response = client.get("/simple-object/")

                assertSoftly {
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Hello, world!"
                }
            }
        }
    }
}