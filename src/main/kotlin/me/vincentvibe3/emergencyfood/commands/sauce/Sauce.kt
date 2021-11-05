package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.internals.GenericCommand
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.internals.SubCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

@Bot.Beta
object Sauce: GenericCommand(), SlashCommand {

    override val name = "sauce"

    override val command = CommandData(name, "Play a song or resume playback")
        .addSubcommands(
            Random.subCommand,
            Read.subCommand
        )

    init {
        registerSubCommands(Read)
        registerSubCommands(Random)
    }

    override suspend fun handle(event: SlashCommandEvent) {
        (subCommands[event.subcommandName] as SubCommand).handle(event)
    }


}