package com.example.service

import com.example.model.SimpleObject
import com.example.plugins.executeQuery
import java.util.UUID


/**
 * Created on 28.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class SimpleObjectService {

    suspend fun create(name: String, value: String): SimpleObject = executeQuery {
        SimpleObject.new {
            this.name = name
            this.value = value
        }
    }

    suspend fun getById(id: UUID): SimpleObject = executeQuery {
        SimpleObject[id]
    }

    suspend fun getAll(): List<SimpleObject> = executeQuery {
        SimpleObject.all().toList()
    }
}