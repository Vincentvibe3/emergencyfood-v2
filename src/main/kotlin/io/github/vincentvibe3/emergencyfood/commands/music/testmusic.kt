package io.github.vincentvibe3.emergencyfood.commands.music

import com.github.Vincentvibe3.efplayer.formats.webm.EBMLHeader
import com.github.Vincentvibe3.efplayer.formats.webm.WebmDocument
import io.github.vincentvibe3.emergencyfood.commands.music.Play.getOptions
import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.audio.Player
import io.github.vincentvibe3.emergencyfood.utils.audio.player.AudioHandler
import io.github.vincentvibe3.emergencyfood.utils.audio.player.AudioHandlerExp
import io.github.vincentvibe3.emergencyfood.utils.audio.player.AudioPlayer
import io.github.vincentvibe3.emergencyfood.utils.audio.player.Queue
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.nio.file.Files
import java.nio.file.Path

object testmusic:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "testmusic"

    override val command = CommandData(name, "Skip the currently playing song")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    private fun connect(channel: VoiceChannel, player: Player){
        val guild = channel.guild
        val audioManager = guild.audioManager
        audioManager.sendingHandler = player.getAudioHandler()
        audioManager.openAudioConnection(channel)
    }

    override suspend fun handle(event: SlashCommandEvent) {
        val channel = event.member?.voiceState?.channel
        event.deferReply().queue()
//        val queue = Queue()
//        queue.getTracks("https://www.youtube.com/watch?v=I0kytvnHG-Q")
//        val stream = queue.queue.first.getStream()
//        val player = AudioPlayer()
        val options = event.options
        val songOption = if (options.isEmpty()){
            null
        } else{
            var song = ""
            options.forEach { song+=" $it" }
            song
        }
        val player = com.github.Vincentvibe3.efplayer.core.Player()
        if (songOption != null) {
            player.play(songOption)
        }
//        if (stream != null) {
            val guild = channel?.guild
            val audioManager = guild?.audioManager
            if (audioManager != null) {
                println("setting audio handler")
                audioManager.sendingHandler = AudioHandlerExp(player)
                audioManager.openAudioConnection(channel)
            }

//        }
        event.hook.editOriginal("playing").queue()
//        event.hook.editOriginal("playing $stream").queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
    }
}