package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import me.vincentvibe3.emergencyfood.utils.audio.SongSearch
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Play:SlashCommand {

    override val name = "play"

    @Bot.Beta
    override val command = CommandData(name, "play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    override fun handle(event: SlashCommandEvent?) {
        event?.deferReply()?.queue()
        val guild = event?.guild?.id
        val player = guild?.let { PlayerManager.getPlayer(it) }
        val audioManager = event?.guild?.audioManager
        audioManager?.sendingHandler = player?.handler
        val channel = event?.member?.voiceState?.channel
        audioManager?.openAudioConnection(channel)
        val songOption = event?.getOption("song")?.asString
        val track = if (songOption?.startsWith("https://www.youtube.com/watch?v=") == true || songOption?.startsWith("https://youtu.be/") == true){
            songOption
        } else {
            if (songOption != null) {
                SongSearch.getSong(songOption)
            } else {
                null
            }
        }
        if (track != null) {
            player?.play(track)
        }
        event?.hook?.editOriginal("Play was called")?.queue()
    }
}