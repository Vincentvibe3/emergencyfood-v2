package me.vincentvibe3.emergencyfood.commands.admin

import me.vincentvibe3.emergencyfood.internals.MessageCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object Admin: MessageCommand() {
    override val name = "admin"

    init {
        registerSubCommands(Kill)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        if (event.author.id==System.getenv("BOT_OWNER")){
            handleSubCommands(event)
        }
    }
}