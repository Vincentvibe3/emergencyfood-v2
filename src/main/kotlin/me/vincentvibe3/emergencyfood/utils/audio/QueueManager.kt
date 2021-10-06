package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.ConfigData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class QueueManager : AudioEventAdapter() {

    var queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
    lateinit var updatesChannel: String
    var loop = false

    fun addToQueue(track: AudioTrack){
        val response = queue.offer(track)
        if (!response){
            throw QueueAddException()
        }
    }

    override fun onPlayerPause(player: AudioPlayer?) {
        super.onPlayerPause(player)
    }

    override fun onPlayerResume(player: AudioPlayer?) {
        super.onPlayerResume(player)
    }

    fun rebuildQueue(){
        val newQueue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
        queue.forEach{
            newQueue.offer(it.makeClone())
        }
        queue = newQueue
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (endReason != null) {
            if (endReason.mayStartNext){
                println("playing next")
                if (queue.indexOf(track)==queue.size-1&&!loop) {
                    println("cleared queue")
                    queue.clear()
                } else if (queue.indexOf(track)==queue.size-1&&loop){
                    println("looping")
                    rebuildQueue()
                    player?.playTrack(queue.elementAt(0))

                } else {
                    player?.playTrack(queue.elementAt(queue.indexOf(track)+1))
                }

            }
        }
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        val client = Bot.getClientInstance()
        val channel = client.getTextChannelById(updatesChannel)

        if (channel != null && track != null) {
            val embed = EmbedBuilder()
                .setTitle("Now Playing")
                .setDescription("[${track.info.title}](${track.info.uri})")
                .setColor(ConfigData.musicEmbedColor)
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        }
//        super.onTrackStart(player, track)
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        super.onTrackException(player, track, exception)
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        super.onTrackStuck(player, track, thresholdMs)
    }
}