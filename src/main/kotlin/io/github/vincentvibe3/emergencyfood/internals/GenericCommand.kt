package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.core.Bot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class GenericCommand {

    abstract val name: String
    open val beta = false

    val subCommands = HashMap<String, GenericSubCommand>()

    fun registerSubCommands(subCommand: GenericSubCommand) {
        this.subCommands[subCommand.name] = subCommand
    }

    suspend fun handleSubCommands(event: SlashCommandInteractionEvent) {
        val subCommand = subCommands[event.subcommandName]
        if (subCommand != null) {
            (subCommand as SubCommand).handle(event)
        }

    }

    suspend fun handleMessageSubCommands(event: MessageReceivedEvent) {
        val options = event.getOptions()
        if (options.isEmpty()) {
            event.guildChannel.sendMessage("Please pass a valid subcommand").queue()
        } else {
            val subcommand = options[0]
            if (subCommands.containsKey(subcommand)) {
                (subCommands[subcommand] as MessageSubCommand).handle(event)
            } else {
                event.guildChannel.sendMessage("Invalid subcommand supplied").queue()
            }
        }

    }

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
        return if (splitMessage.size > 1) {
            splitMessage.subList(1, splitMessage.size)
        } else {
            ArrayList()
        }
    }

}