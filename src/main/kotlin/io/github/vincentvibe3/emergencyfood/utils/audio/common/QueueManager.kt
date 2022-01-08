package io.github.vincentvibe3.emergencyfood.utils.audio.common

import com.github.Vincentvibe3.efplayer.core.Track
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Logging
import io.github.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.MessageBuilder
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class QueueManager(val commonPlayer: CommonPlayer) {

    var queue: BlockingDeque<Any> = LinkedBlockingDeque()
    lateinit var updatesChannel: String
    var loop = false
    private var lastUpdatesMessage:String? = null
    private var lastUpdatesChannel:String? = null

    fun addToQueue(track: AudioTrack):Boolean{
        commonPlayer.loadQueue.take().value = queue.size
        return queue.offer(track)
    }

    fun addToQueue(track: Track):Boolean{
        commonPlayer.loadQueue.take().value = queue.size
        return queue.offer(track)
    }

    //is used when looping to avoid replaying AudioTracks
    fun rebuildQueue(){
        val newQueue: BlockingDeque<Any> = LinkedBlockingDeque()
        queue.forEach{
            if (it is AudioTrack){
                newQueue.offer(it.makeClone())
            } else {
                newQueue.offer(it)
            }
        }
        queue = newQueue
    }

    suspend fun onTrackEnd(track: Any, canStartNext:Boolean) {
        val client = Bot.getClientInstance()
        //delete the old now playing message if it exists
        val lastChannel = lastUpdatesChannel?.let { client.getTextChannelById(it) }
        if (lastChannel != null) {
            val lastMessage = lastUpdatesMessage?.let { lastChannel.retrieveMessageById(it) }
            lastMessage?.queue({it.delete().queue()}, { println("Failed to get old message")})
        }
        var next: Any? = null
        //clear on end
        if (canStartNext){
            if (queue.indexOf(track)==queue.size-1&&!loop) {
                queue.clear()
                //prepare queue and loop
            } else if (queue.indexOf(track)==queue.size-1&&loop){
                rebuildQueue()
                next = queue.elementAt(0)
                //play next
            } else {
                next = queue.elementAt(queue.indexOf(track)+1)
            }
            if (next is Track){
                commonPlayer.efPlayer.play(next)
            } else if (next is AudioTrack){
                commonPlayer.lavaplayer.playTrack(next)
            }
        }

    }

    fun onTrackStart(track: Any) {
        val client = Bot.getClientInstance()
        client.getGuildById(commonPlayer.guild)?.audioManager?.sendingHandler = commonPlayer.getAudioHandler()
        val channel = client.getTextChannelById(updatesChannel)
        var title: String? = null
        var url:String? = null
        if (track is AudioTrack){
            title = track.info.title
            url = track.info.uri
        } else if (track is Track){
            title = track.title
            url = track.url
        }
        //send now playing message
        if (channel != null && title!=null && url != null) {
            val embed = Templates.getMusicEmbed()
                .setTitle("Now Playing")
                .setDescription("[$title]($url)")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue(
                {lastUpdatesMessage = it.id
                lastUpdatesChannel = it.channel.id},
                { Logging.logger.error("Failed to send update message in ${channel.guild}")}
            )
        }
    }

    suspend fun onTrackException(track: Any) {
        val trackIndex = queue.indexOf(track)
        //clear on end
        var next: Any? = null
        if (trackIndex==queue.size-1&&!loop) {
            queue.clear()
            //prepare queue and loop
        } else if (trackIndex==queue.size-1&&loop){
            rebuildQueue()
            next = queue.elementAt(0)
            queue.remove(queue.elementAt(trackIndex))
            //play next
        } else {
            next = queue.elementAt(trackIndex+1)
            queue.remove(queue.elementAt(trackIndex))
        }
        if (next is Track){
            commonPlayer.efPlayer.play(next)
        } else if (next is AudioTrack){
            commonPlayer.lavaplayer.playTrack(next)
        }
        val client = Bot.getClientInstance()
        val channel = client.getTextChannelById(updatesChannel)
        channel?.sendMessage("An error occurred during playback")?.queue(
            {},
            { Logging.logger.error("Failed to send playback failure message in ${channel.guild}")}
        )

    }

    suspend fun onTrackStuck(track: AudioTrack?) {
        val trackIndex = queue.indexOf(track)
        //clear on end
        var next: Any? = null
        if (trackIndex==queue.size-1&&!loop) {
            queue.clear()
            //prepare queue and loop
        } else if (trackIndex==queue.size-1&&loop){
            rebuildQueue()
            next = queue.elementAt(0)
            queue.remove(queue.elementAt(trackIndex))
            //play next
        } else {
            next = queue.elementAt(trackIndex+1)
            queue.remove(queue.elementAt(trackIndex))
        }
        if (next is Track){
            commonPlayer.efPlayer.play(next)
        } else if (next is AudioTrack){
            commonPlayer.lavaplayer.playTrack(next)
        }
        val client = Bot.getClientInstance()
        val channel = client.getTextChannelById(updatesChannel)
        channel?.sendMessage("An error occurred during playback")?.queue(
            {},
            { Logging.logger.error("Failed to send playback failure message in ${channel.guild}")}
        )

    }
}