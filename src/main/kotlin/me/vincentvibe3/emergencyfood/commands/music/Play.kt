package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

class Play:SlashCommand {

    override val name = "play"

    @Bot.Beta
    override val command = Bot.getClientInstance()
        .upsertCommand(name, "play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    override fun handle(event: SlashCommandEvent?) {
        event?.deferReply()
        event?.reply("Play was called")
    }
}