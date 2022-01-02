package io.github.vincentvibe3.emergencyfood.utils.audio.player

import com.github.Vincentvibe3.EfPlayer.formats.webm.WebmDocument
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandlerExp(private var doc: WebmDocument) : AudioSendHandler {

    override fun canProvide(): Boolean {
        return doc.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        return doc.getAudio()
    }

    override fun isOpus(): Boolean {
        return true
    }
}