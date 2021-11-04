package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class UniversalCommand():SlashCommand() {

    abstract suspend fun handle(event: MessageReceivedEvent)

    suspend fun handleSubCommands(event: MessageReceivedEvent){
        val options = event.getOptions()
        if (options.isEmpty()){
            event.textChannel.sendMessage("Please pass a valid subcommand")
        } else {
            val subcommand = options[0]
            if (subCommands.containsKey(subcommand)){
                (subCommands[subcommand] as MessageSubCommand).handle(event)
            } else {
                event.textChannel.sendMessage("Invalid subcommand supplied")
            }
        }

    }

    fun MessageReceivedEvent.getOptions(): List<String> {
        val message = this.message.contentDisplay
            .replace("@$name", "")
            .replace(Templates.prefix, "")
            .trim()
        val splitMessage = message.split(" ")
        return if (splitMessage.size > 1){
            splitMessage.subList(1, splitMessage.size-1)
        } else {
            ArrayList()
        }
    }

    fun MessageReceivedEvent.getSubCommandOptions(): List<String> {
        val options = this.getOptions()
        return if (options.size > 1){
            options.subList(2, options.size-1)
        } else {
            ArrayList()
        }
    }

}