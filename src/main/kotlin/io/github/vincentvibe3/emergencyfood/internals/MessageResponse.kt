package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageResponse {

    val user:String
    val channel:String

    fun handle(event: MessageReceivedEvent)

}