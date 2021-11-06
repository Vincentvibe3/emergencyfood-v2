package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class GenericSubCommand {

    abstract val name: String

    fun MessageReceivedEvent.getOptions(): List<String> {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val displayName = client.guilds.first { it.id == this.guild.id }.selfMember.effectiveName
        val message = this.message.contentDisplay
            .replace("@$displayName", "")
            .replace(Templates.prefix, "")
            .trim()
        val splitMessage = message.split(" ")
        return if (splitMessage.size > 1){
            splitMessage.subList(1, splitMessage.size-1)
        } else {
            ArrayList()
        }
    }

}