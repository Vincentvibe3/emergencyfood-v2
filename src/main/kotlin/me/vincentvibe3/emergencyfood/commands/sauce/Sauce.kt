package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.commands.music.Play
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object Sauce:SlashCommand {
    override val name = "sauce"

    override val command = CommandData(Play.name, "Play a song or resume playback")
        .addSubcommands(
            SubcommandData("random", "Get a random sauce"),
            SubcommandData("read", "Read a sauce")
                .addOption(OptionType.STRING,"numbers", "the number to the sauce")
        )

    override fun handle(event: SlashCommandEvent) {
        TODO("Not yet implemented")
    }


}