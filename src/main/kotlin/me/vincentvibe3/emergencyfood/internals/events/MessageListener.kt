package me.vincentvibe3.emergencyfood.internals.events

import kotlinx.coroutines.*
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.internals.MessageCommand
import me.vincentvibe3.emergencyfood.internals.MessageCommandManager
import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object MessageListener: ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val name = client.guilds.first { it.id == event.guild.id }.selfMember.effectiveName
        val selfMember = event.guild.getMemberById(selfId)
        if (event.message.mentionedMembers.contains(selfMember)||event.message.contentDisplay.startsWith(Templates.prefix)){
            Logging.logger.debug("MessageCommand received")
            val message = event.message.contentDisplay.replace("@$name", "").trim()
            val commandName = message.split(" ")[0]
            GlobalScope.launch {
                retrieveCommand(commandName)?.handle(event, message)
            }
        }
    }

    private fun retrieveCommand(name:String): MessageCommand?{
        return MessageCommandManager.getCommands()[name]
    }
}
