package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.commands.music.Play
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.SubCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Sauce: SlashCommand() {

    override val name = "sauce"

    override val command = CommandData(Play.name, "Play a song or resume playback")
        .addSubcommands(
            Random.subCommand,
            Read.subCommand
        )

    init {
        registerSubCommands(Read)
        registerSubCommands(Random)
    }

    override fun handle(event: SlashCommandEvent) {
        subCommands[event.subcommandName]?.handle(event)
    }


}