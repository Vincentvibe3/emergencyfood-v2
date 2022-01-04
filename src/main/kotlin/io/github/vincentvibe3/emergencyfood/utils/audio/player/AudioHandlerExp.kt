package io.github.vincentvibe3.emergencyfood.utils.audio.player

import com.github.Vincentvibe3.efplayer.core.Player
import com.github.Vincentvibe3.efplayer.core.Track
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandlerExp(private var doc: Player) : AudioSendHandler {

    override fun canProvide(): Boolean {
        return doc.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        return ByteBuffer.wrap(doc.provide())
    }

    override fun isOpus(): Boolean {
        return true
    }
}