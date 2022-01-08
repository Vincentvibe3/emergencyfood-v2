package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageCommand {

    abstract suspend fun handle(event: MessageReceivedEvent)

}