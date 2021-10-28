package me.vincentvibe3.emergencyfood.utils

import me.vincentvibe3.emergencyfood.commands.sauce.Sauce
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

abstract class SlashCommand {

    //name of the command
    abstract val name: String

    //command to create
    abstract val command: CommandData

    val subCommands = HashMap<String, SubCommand>()

    fun registerSubCommands(subCommand: SubCommand){
        this.subCommands[subCommand.name] = subCommand
    }

    //handle events
    abstract suspend fun handle(event: SlashCommandEvent)

}
