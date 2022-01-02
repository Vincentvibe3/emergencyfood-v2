package io.github.vincentvibe3.emergencyfood.commands.music

import com.github.Vincentvibe3.EfPlayer.formats.webm.EBMLHeader
import com.github.Vincentvibe3.EfPlayer.formats.webm.WebmDocument
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
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.nio.file.Files
import java.nio.file.Path

object testmusic:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "testmusic"

    override val command = CommandData(name, "Skip the currently playing song")

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

        val doc = WebmDocument()
        doc.header = EBMLHeader()
        val input = Files.newInputStream(Path.of("C:\\Users\\Vincent\\Downloads\\videoplayback2.webm"))
        doc.header.parseHeader(input)
        input.readNBytes(4)
        doc.readSegment(input)

//        if (stream != null) {
            val guild = channel?.guild
            val audioManager = guild?.audioManager
            if (audioManager != null) {
                audioManager.sendingHandler = AudioHandlerExp(doc)
                audioManager.openAudioConnection(channel)
            }

//        }
        event.hook.editOriginal("playing").queue()
//        event.hook.editOriginal("playing $stream").queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
    }
}