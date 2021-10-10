package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageCommand {

    val name:String

    fun handle(event: MessageReceivedEvent, message:String)

}