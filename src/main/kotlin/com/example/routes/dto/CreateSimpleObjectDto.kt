package com.example.routes.dto

import kotlinx.serialization.Serializable


/**
 * Created on 28.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
@Serializable
data class CreateSimpleObjectDto(
    val name: String,
    val value: String
                                )
