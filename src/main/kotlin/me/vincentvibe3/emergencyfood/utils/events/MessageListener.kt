package me.vincentvibe3.emergencyfood.utils.events

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.MessageCommand
import me.vincentvibe3.emergencyfood.utils.MessageCommandManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object MessageListener:ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val name = client.selfUser.name
        val selfMember = event.guild.getMemberById(selfId)
        if (event.message.mentionedMembers.contains(selfMember)){
            Logging.logger.debug("MessageCommand received")
            val message = event.message.contentDisplay.replace("@$name", "").trim()
            val commandName = message.split(" ")[0]
            retrieveCommand(commandName)?.handle(event, message)
        }
    }

    private fun retrieveCommand(name:String): MessageCommand?{
        return MessageCommandManager.getCommands()[name]
    }
}