package io.github.vincentvibe3.emergencyfood.commands.admin

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object Admin: GenericCommand(), MessageCommand {
    override val name = "admin"

    init {
        registerSubCommands(Kill)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        if (event.author.id==System.getenv("BOT_OWNER")){
            handleMessageSubCommands(event)
        }
    }
}