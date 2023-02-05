@file:Suppress("PropertyName")

package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    val Stable:ConfigScopeData?=null,
    val Beta:ConfigScopeData?=null,
    val Local:ConfigScopeData?=null,
    val Global:ConfigScopeData?=null,
    val Channel:String?=null
){

    init{
        require(listOf("Local", "Stable", "Beta").contains(Channel))
    }

    @Serializable
    data class ConfigScopeData(
        val token:String?=null,
        val exclusions:List<String>?=null,
        val testServer:String?=null,
        val prefix:String?=null,
        val owner:String?=null,
        val status:String?=null,
        val remote:String?=null,
        val logflareUrl:String?=null,
        val logflareKey:String?=null,
        val envName:String?=null,
        val supabaseKey:String?=null,
        val supabaseUrl:String?=null
    )
}
