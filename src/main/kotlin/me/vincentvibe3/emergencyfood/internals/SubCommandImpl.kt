package me.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

abstract class SubCommandImpl:GenericSubCommand() {

    abstract val subCommand:SubcommandData

    abstract suspend fun handle(event: SlashCommandEvent)

}