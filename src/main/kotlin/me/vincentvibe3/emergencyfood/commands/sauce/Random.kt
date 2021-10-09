package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.utils.SubCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

object Random:SubCommand {
    override fun handle(event: SlashCommandEvent) {
        if (event.textChannel.isNSFW){

        } else {
            event.reply("You must use a NSFW channel for this").queue()
        }
    }
}