package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.internals.SubCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object Read: SubCommand {
    override val name = "read"

    override val subCommand = SubcommandData("read", "Read a sauce")
        .addOption(OptionType.STRING,"numbers", "the number to the sauce")

    override suspend fun handle(event: SlashCommandEvent) {
        if (event.textChannel.isNSFW){

        } else {
            event.reply("You must use a NSFW channel for this").queue()
        }
    }
}