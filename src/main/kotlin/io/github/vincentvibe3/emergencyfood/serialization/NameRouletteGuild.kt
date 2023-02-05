@file:Suppress("PropertyName")

package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class NameRouletteGuild(
    val id:String,
    val channel_id:String,
    val ping_day_of_week:Int,
    val ping_hour:Int,
    val ping_min:Int,
    var last_message:String?=null,
    var current_deathroll:String?=null
)
