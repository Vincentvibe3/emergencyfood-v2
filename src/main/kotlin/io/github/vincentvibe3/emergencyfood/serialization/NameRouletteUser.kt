@file:Suppress("PropertyName")

package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class NameRouletteUser(
    val id:String,
    val guild:Long,
    var roll_count:Int,
    var deathroll:Boolean,
    val roll_names:ArrayList<String>,
    var added_choices:Int,
    var added_choices_death: Int
)
