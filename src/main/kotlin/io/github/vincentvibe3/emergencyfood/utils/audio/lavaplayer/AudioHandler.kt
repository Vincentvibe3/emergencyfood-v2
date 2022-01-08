package io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandler(private var player: AudioPlayer) : AudioSendHandler {

    private val frame = MutableAudioFrame()
    private var buffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())

    init {
        frame.setBuffer(buffer)
    }

    override fun canProvide(): Boolean {
        return player.provide(frame)
    }

    override fun provide20MsAudio(): ByteBuffer? {
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }
}