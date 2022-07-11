package io.github.vincentvibe3.emergencyfood.internals.events

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.CommandManager
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageResponseManager
import io.github.vincentvibe3.emergencyfood.utils.Logging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val client = Bot.getClientInstance()
        val name = client.guilds.first { it.id == event.guild.id }.selfMember.effectiveName
        val response = MessageResponseManager.get(event.author.id, event.channel.id)
        if (response != null) {
            MessageResponseManager.remove(response)
            response.handle(event)
        }
        if (checkMessageForCommand(event)) {
            Logging.logger.debug("MessageCommand received")
            val message = event.message.contentDisplay.replace("@$name", "").replace(Config.prefix, "").trim()
            val commandName = message.split(" ")[0]
            GlobalScope.launch {
                retrieveCommand(commandName)?.handle(event)
            }
        }
    }

    private fun checkMessageForCommand(event: MessageReceivedEvent): Boolean {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val name = client.guilds.first { it.id == event.guild.id }.selfMember.effectiveName
        val message = event.message
        return if (message.contentDisplay.startsWith(Config.prefix)) {
            true
        } else if (message.mentions.members.contains(selfMember)) {
            message.contentDisplay.startsWith("@$name")
        } else {
            false
        }
    }

    private fun retrieveCommand(name: String): MessageCommand? {
        return CommandManager.getMessageCommands()[name]
    }
}
