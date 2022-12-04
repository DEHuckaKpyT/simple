package com.example.converter

import com.example.model.SimpleObject
import com.example.routes.dto.SimpleObjectDto
import org.mapstruct.Mapper


/**
 * Created on 28.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
@Mapper
interface SimpleObjectConverter {

    fun toSimpleObjectDto(simpleObject: SimpleObject): SimpleObjectDto
    fun toSimpleObjectDto(simpleObjects: List<SimpleObject>): List<SimpleObjectDto>
}