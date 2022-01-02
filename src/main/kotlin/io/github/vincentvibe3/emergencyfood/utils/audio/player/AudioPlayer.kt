package io.github.vincentvibe3.emergencyfood.utils.audio.player

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.audio.OpusPacket
import net.dv8tion.jda.internal.audio.AudioPacket
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class AudioPlayer {

    lateinit var currentTrack:Track
    private val queue: ConcurrentLinkedQueue<ByteArray> = ConcurrentLinkedQueue()

    suspend fun startStreaming(url:String){
        val client = HttpClient(CIO)
        client.get<HttpStatement>(url).execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.receive()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes(OpusPacket.OPUS_FRAME_SIZE)
                    queue.add(bytes)
                    println("adding data")
                }
            }
        }
        println("done")
    }

    fun canProvide():Boolean{
        return !queue.isEmpty()
    }

    fun provide(): ByteBuffer? {
        val data = queue.poll()
        println("providing")
        return if (data == null) null else ByteBuffer.wrap(data)
    }

    suspend fun loadTrack(url:String){
        withContext(Dispatchers.IO) {
            startStreaming(url)
        }
    }

    fun pause(){

    }



}