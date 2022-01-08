package io.github.vincentvibe3.emergencyfood.utils.audio.efplayer

import com.github.Vincentvibe3.efplayer.core.Player
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandlerEf(private var player: Player) : AudioSendHandler {

    val buffer = ByteBuffer.allocate(22500000)

    override fun canProvide(): Boolean {
        return player.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        buffer.clear()
        buffer.put(player.provide())
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }
}