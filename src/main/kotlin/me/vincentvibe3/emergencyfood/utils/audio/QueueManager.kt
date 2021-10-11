package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class QueueManager : AudioEventAdapter() {

    var queue: BlockingDeque<AudioTrack> = LinkedBlockingDeque()
    lateinit var updatesChannel: String
    var loop = false
    private var lastUpdatesMessage:String? = null
    private var lastUpdatesChannel:String? = null

    fun addToQueue(track: AudioTrack):Boolean{
        return queue.offer(track)
    }

    override fun onPlayerPause(player: AudioPlayer?) {
        super.onPlayerPause(player)
    }

    override fun onPlayerResume(player: AudioPlayer?) {
        super.onPlayerResume(player)
    }

    //is used when looping to avoid replaying AudioTracks
    fun rebuildQueue(){
        val newQueue: BlockingDeque<AudioTrack> = LinkedBlockingDeque()
        queue.forEach{
            newQueue.offer(it.makeClone())
        }
        queue = newQueue
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        val client = Bot.getClientInstance()
        //delete the old now playing message if it exists
        val lastChannel = lastUpdatesChannel?.let { client.getTextChannelById(it) }
        if (lastChannel != null) {
            val lastMessage = lastUpdatesMessage?.let { lastChannel.retrieveMessageById(it) }
            lastMessage?.queue({it.delete().queue()}, { println("failed to get old message")})
        }

        if (endReason != null) {
            if (endReason.mayStartNext){
                //clear on end
                if (queue.indexOf(track)==queue.size-1&&!loop) {
                    queue.clear()
                //prepare queue and loop
                } else if (queue.indexOf(track)==queue.size-1&&loop){
                    rebuildQueue()
                    player?.playTrack(queue.elementAt(0))
                //play next
                } else {
                    player?.playTrack(queue.elementAt(queue.indexOf(track)+1))
                }

            }
        }
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        val client = Bot.getClientInstance()
        val channel = client.getTextChannelById(updatesChannel)
        //send now playing message
        if (channel != null && track != null) {
            val embed = EmbedBuilder()
                .setTitle("Now Playing")
                .setDescription("[${track.info.title}](${track.info.uri})")
                .setColor(ConfigData.musicEmbedColor)
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

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        super.onTrackException(player, track, exception)
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        super.onTrackStuck(player, track, thresholdMs)
    }
}