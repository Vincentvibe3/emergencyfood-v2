package me.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class MessageCommand {

    abstract val name:String

    abstract suspend fun handle(event: MessageReceivedEvent, message:String)

}