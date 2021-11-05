package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface MessageSubCommand {

    suspend fun handle(event: MessageReceivedEvent)

}
