package io.github.vincentvibe3.emergencyfood.commands.admin

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.system.exitProcess

object Kill:GenericSubCommand(), MessageSubCommand {

    override val name = "kill"

    override suspend fun handle(event: MessageReceivedEvent) {
        Logging.logger.info("Requested to Shutdown. Now Stopping")
        Bot.getClientInstance().shutdownNow()
        exitProcess(0)
    }

}