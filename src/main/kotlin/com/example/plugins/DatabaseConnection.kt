package com.example.plugins

import com.example.model.SimpleObjects
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Created on 29.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureDatabase() {

    val config = environment.config

    fun connect() {
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.property("ktor.db.jdbcUrl").getString()
            username = config.property("ktor.db.dbUser").getString()
            password = config.property("ktor.db.dbPassword").getString()
            maximumPoolSize = 4
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(
                    SimpleObjects
            )
        }
    }

    connect()
    createTables()
}

suspend fun <T> executeQuery(block: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                addLogger(StdOutSqlLogger)
                block()
            }
        }