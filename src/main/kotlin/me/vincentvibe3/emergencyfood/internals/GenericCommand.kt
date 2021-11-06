package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class GenericCommand {

    abstract val name: String

    val subCommands = HashMap<String, GenericSubCommand>()

    fun registerSubCommands(subCommand: GenericSubCommand) {
        this.subCommands[subCommand.name] = subCommand
    }

    suspend fun handleSubCommands(event: MessageReceivedEvent) {
        val options = event.getOptions()
        if (options.isEmpty()) {
            event.textChannel.sendMessage("Please pass a valid subcommand")
        } else {
            val subcommand = options[0]
            if (subCommands.containsKey(subcommand)) {
                (subCommands[subcommand] as MessageSubCommand).handle(event)
            } else {
                event.textChannel.sendMessage("Invalid subcommand supplied")
            }
        }

    }

    fun MessageReceivedEvent.getOptions(): List<String> {
        val client = Bot.getClientInstance()
        val memberName = client.guilds.first { it.id == this.guild.id }.selfMember.effectiveName
        val message = if (this.message.contentDisplay.startsWith(Templates.prefix)) {
            this.message.contentDisplay
                .replaceFirst(Templates.prefix, "")
                .trim()
        } else {
            this.message.contentDisplay
                .replaceFirst("@$memberName", "")
                .trim()
        }
        val splitMessage = message.split(" ")
        return if (splitMessage.size > 1) {
            splitMessage.subList(1, splitMessage.size)
        } else {
            ArrayList()
        }
    }

    fun MessageReceivedEvent.getSubCommandOptions(): List<String> {
        val options = this.getOptions()
        return if (options.size > 1) {
            options.subList(2, options.size - 1)
        } else {
            ArrayList()
        }
    }

}