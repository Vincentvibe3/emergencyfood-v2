package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

interface SlashCommand {

    //name of the command
    val name: String

    //command to create
    val command: CommandCreateAction

    //handle events
    fun handle(event: SlashCommandEvent?)

}
