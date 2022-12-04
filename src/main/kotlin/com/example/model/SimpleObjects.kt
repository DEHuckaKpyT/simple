package com.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*


/**
 * Created on 28.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
object SimpleObjects : UUIDTable() {

    val name: Column<String> = varchar("name", 100)
    val value: Column<String> = varchar("value", 100)
}

class SimpleObject(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SimpleObject>(SimpleObjects)

    var name by SimpleObjects.name
    var value by SimpleObjects.value
}