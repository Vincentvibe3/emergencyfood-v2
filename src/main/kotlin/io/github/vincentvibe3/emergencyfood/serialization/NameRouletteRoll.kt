@file:Suppress("PropertyName")

package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class NameRouletteRoll(
    val name:String,
    val guild:Long,
    val added_by:String,
    val deathroll:Boolean,
    val id:Int?=null,
)
