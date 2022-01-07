package io.github.vincentvibe3.emergencyfood.utils.audio.player

import com.github.Vincentvibe3.efplayer.core.Player
import com.github.Vincentvibe3.efplayer.core.Track
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandlerExp(private var doc: Player) : AudioSendHandler {

    val buffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())

    override fun canProvide(): Boolean {
        return doc.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        buffer.clear()
        buffer.put(doc.provide())
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }
}