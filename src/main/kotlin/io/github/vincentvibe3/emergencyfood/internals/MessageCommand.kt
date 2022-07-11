package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageCommand {

    suspend fun handle(event: MessageReceivedEvent)

}