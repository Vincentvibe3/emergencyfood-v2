package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class LogflareMessage(
    val message:String,
    val metadata:LogflareMetadata
){
    @Serializable
    data class LogflareMetadata(
        val level:String,
        val loggerName:String,
        val thread:String,
        val timestamp:Long,
        val environment:String
    )
}