package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

class Play:SlashCommand {

    override val name = "play"

    @Bot.Beta
    override val command = Bot.getClientInstance()
        .upsertCommand(name, "play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    override fun handle(event: SlashCommandEvent?) {
        event?.deferReply()?.queue()
        event?.hook?.editOriginal("Play was called")?.queue()
    }
}