package io.github.vincentvibe3.emergencyfood.utils.audio.efplayer

import com.github.Vincentvibe3.efplayer.core.Player
//import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioHandlerEf(private var player: Player) : AudioSendHandler {

    private var buffer: ByteBuffer = ByteBuffer.allocate(1500)

    override fun canProvide(): Boolean {
        return player.canProvide()
    }

    override fun provide20MsAudio(): ByteBuffer {
        val bytes = player.provide()
        if (buffer.capacity()<bytes.size){
            buffer = ByteBuffer.allocate(bytes.size)
        }
        buffer.clear()
        buffer.put(player.provide())
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }


}