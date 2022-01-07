package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import com.github.Vincentvibe3.efplayer.core.Player
import io.github.vincentvibe3.emergencyfood.utils.audio.player.AudioHandlerExp
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object testmusic:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "testmusic"

    override val command = CommandData(name, "Skip the currently playing song")
        .addOption(OptionType.STRING ,"song", "link or search query", true)

    override suspend fun handle(event: SlashCommandEvent) {
        val channel = event.member?.voiceState?.channel
        event.deferReply().queue()
//        val queue = Queue()
//        queue.getTracks("https://www.youtube.com/watch?v=I0kytvnHG-Q")
//        val stream = queue.queue.first.getStream()
//        val player = AudioPlayer()
        val songOption = event.getOption("song")?.asString
        if (songOption != null) {
            if (songOption=="stop") {
                Players.player.stop()
            }else if (songOption=="pause") {
                Players.player.pause()
            }else if (songOption=="resume"){
                Players.player.resume()
            } else {
                Players.player.load(songOption)
            }
        }
//        if (stream != null) {
            val guild = channel?.guild
            val audioManager = guild?.audioManager
            if (audioManager != null) {
                println("setting audio handler")
                audioManager.sendingHandler = AudioHandlerExp(Players.player)
                audioManager.openAudioConnection(channel)
            }

//        }
        event.hook.editOriginal("playing").queue()
//        event.hook.editOriginal("playing $stream").queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
    }
}