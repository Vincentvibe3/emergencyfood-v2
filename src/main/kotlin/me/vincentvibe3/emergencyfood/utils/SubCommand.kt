package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface SubCommand {

    val name: String

    val subCommand:SubcommandData

    fun handle(event: SlashCommandEvent)

}