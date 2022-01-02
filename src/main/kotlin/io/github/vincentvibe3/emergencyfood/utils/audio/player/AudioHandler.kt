package io.github.vincentvibe3.emergencyfood.utils.audio.player

import club.minnced.opus.util.OpusLibrary
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.format.transcoder.OpusChunkDecoder
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.OpusPacket
import java.nio.ByteBuffer

class AudioHandler(private var player: AudioPlayer) : AudioSendHandler {

    override fun canProvide(): Boolean {
        return player.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        return player.provide()
    }

    override fun isOpus(): Boolean {
        return true
    }
}