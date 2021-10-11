package me.vincentvibe3.emergencyfood.commands.admin

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.MessageCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.system.exitProcess

object Admin:MessageCommand {
    override val name = "admin"

    override fun handle(event: MessageReceivedEvent, message:String) {
        if (event.author.id==System.getenv("BOT_OWNER")){
            if (message.replace("admin", "").trim().startsWith("kill")){
                Logging.logger.info("Requested to Shutdown. Now Stopping")
                Bot.getClientInstance().shutdownNow()
                exitProcess(0)
            }
        }
    }
}