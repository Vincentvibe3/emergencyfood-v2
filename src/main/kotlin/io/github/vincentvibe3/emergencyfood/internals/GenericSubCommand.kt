package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class GenericSubCommand {

    abstract val name: String

    fun MessageReceivedEvent.getOptions(): List<String> {
        val client = Bot.getClientInstance()
        val memberName = client.guilds.first { it.id == this.guild.id }.selfMember.effectiveName
        val message = if (this.message.contentDisplay.startsWith(Config.prefix)) {
            this.message.contentDisplay
                .replaceFirst(Config.prefix, "")
                .trim()
        } else {
            this.message.contentDisplay
                .replaceFirst("@$memberName", "")
                .trim()
        }
        val splitMessage = message.split(" ")
        return if (splitMessage.size > 2) {
            splitMessage.subList(2, splitMessage.size)
        } else {
            ArrayList()
        }
    }

}