package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.Player
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import me.vincentvibe3.emergencyfood.utils.audio.SongSearch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Play:SlashCommand {

    override val name = "play"

    @Bot.Beta
    override val command = CommandData(name, "play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    fun getTrack(query:String):String{
        return if (query.startsWith("https://www.youtube.com/watch?v=")||query.startsWith("https://youtu.be/")){
            query
        } else {
            SongSearch.getSong(query)
        }
    }

    fun resume(player:Player){
        player.resume()
    }

    fun connect(channel:VoiceChannel, player: Player){
        val guild = channel.guild
        val audioManager = guild.audioManager
        audioManager.sendingHandler = player.getAudioHandler()
        audioManager.openAudioConnection(channel)


    }

    override fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        val channel = event.member?.voiceState?.channel
        if (player != null && channel != null) {
            connect(channel, player)
        }
        val songOption = event.getOption("song")?.asString
        if (songOption == null && player != null){
            resume(player)
        } else if (songOption != null){
            val track = getTrack(songOption)
            player?.play(track)
        }
        event.hook.editOriginal("Play was called").queue()
    }
}