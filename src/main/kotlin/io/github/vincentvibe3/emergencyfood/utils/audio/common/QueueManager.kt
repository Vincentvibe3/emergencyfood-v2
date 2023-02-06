package io.github.vincentvibe3.emergencyfood.utils.audio.common

import com.github.Vincentvibe3.efplayer.core.Track
//import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.locks.ReentrantLock

class QueueManager(private val commonPlayer: CommonPlayer) {

    var queue: BlockingDeque<Any> = LinkedBlockingDeque()
    lateinit var updatesChannel: String
    var loop = false
    private var lastUpdatesMessage: String? = null
    private var lastUpdatesChannel: String? = null
    val pendingLoad = HashMap<String, Any>()
    private val lock = ReentrantLock()

    fun playlistLoadedMessage(trackCount: Int, loadId:String){
        val message = MessageCreateBuilder()
            .setEmbeds(Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added $trackCount songs from [playlist]($loadId)")
                .build())
            .build()
        val event = pendingLoad[loadId]
        if (event!=null){
            if (event is SlashCommandInteractionEvent){
                event.hook.editOriginal(MessageEditData.fromCreateData(message)).queue()
            } else if (event is MessageReceivedEvent){
                event.channel.sendMessage(message).queue()
            } else {
                Logging.logger.error("Received a non event class while loading")
            }
        } else {
            Logging.logger.error("Failed to get event for loading")
        }
    }

    private fun trackLoadedMessage(track:Any, loadId:String){
        var title = ""
        var url = ""
        if (track is Track){
            url = track.url
            title = track.title?: "No title"
//        }else if (track is AudioTrack){
//            url = track.info.uri
//            title = track.info.title
        }
        val message = MessageCreateBuilder()
            .setEmbeds(Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added [$title]($url)")
                .build())
            .build()
        val event = pendingLoad[loadId]
        if (event!=null){
            if (event is SlashCommandInteractionEvent){
                event.hook.editOriginal(MessageEditData.fromCreateData(message)).queue()
            } else if (event is MessageReceivedEvent){
                event.channel.sendMessage(message).queue()
            } else {
                Logging.logger.error("Received a non event class while loading")
            }
        } else {
            Logging.logger.error("Failed to get event for loading")
        }
    }

//    fun addToQueue(track: AudioTrack, isFromPlaylist: Boolean): Boolean {
//        if (!isFromPlaylist) {
//            trackLoadedMessage(track, track.identifier)
//        }
//        return queue.offer(track)
//    }

    fun addToQueue(track: Track, isFromPlaylist: Boolean): Boolean {
        if (!isFromPlaylist) {
            trackLoadedMessage(track, track.loadId)
        }
        return queue.offer(track)
    }

    //is used when looping to avoid replaying AudioTracks
    fun rebuildQueue() {
        val newQueue: BlockingDeque<Any> = LinkedBlockingDeque()
        queue.forEach {
            if (it is Track) {
                newQueue.offer(it)
//            } else {
//                newQueue.offer(it.makeClone())
            }
        }
        queue = newQueue
    }

    private fun deleteNowPlayingMessage(){
        val client = Bot.getClientInstance()
        //delete the old now playing message if it exists
        synchronized(lock) {
            val lastChannel = lastUpdatesChannel?.let { client.getTextChannelById(it) }
            if (lastChannel != null) {
                val lastMessage = lastUpdatesMessage?.let { lastChannel.retrieveMessageById(it) }
                lastMessage?.queue({
                    it.delete().queue()
                }, { Logging.logger.error("Failed to get old message") })
            }
        }
    }

    fun onTrackEnd(track: Any, canStartNext: Boolean) {
        deleteNowPlayingMessage()
        var next: Any? = null
        //clear on end
        if (canStartNext) {
            if (queue.indexOf(track) == queue.size - 1 && !loop) {
                queue.clear()
                //prepare queue and loop
            } else if (queue.indexOf(track) == queue.size - 1 && loop) {
                rebuildQueue()
                next = queue.elementAt(0)
                //play next
            } else {
                next = queue.elementAt(queue.indexOf(track) + 1)
            }
            if (next is Track) {
                commonPlayer.efPlayer.play(next)
//            } else if (next is AudioTrack) {
//                commonPlayer.lavaplayer.playTrack(next)
            }
        }

    }

    fun onTrackStart(track: Any) {
        val client = Bot.getClientInstance()
        client.getGuildById(commonPlayer.guild)?.audioManager?.sendingHandler = commonPlayer.getAudioHandler()
        val channel = client.getTextChannelById(updatesChannel)
        var title: String? = null
        var url: String? = null
        if (track is Track) {
            title = track.title
            url = track.url
//        } else if (track is AudioTrack) {
//            title = track.info.title
//            url = track.info.uri
        }
        //send now playing message
        if (channel != null && title != null && url != null) {
            val embed = Templates.getMusicEmbed()
                .setTitle("Now Playing")
                .setDescription("[$title]($url)")
                .build()
            val message = MessageCreateBuilder()
                .setEmbeds(embed)
                .build()
            synchronized(lock) {
                channel.sendMessage(message).queue(
                    {
                        lastUpdatesMessage = it.id
                        lastUpdatesChannel = it.channel.id
                    },
                    { Logging.logger.error("Failed to send update message in ${channel.guild}") }
                )
            }
        }
    }

    fun onTrackException(track: Any) {
        deleteNowPlayingMessage()
        val trackIndex = queue.indexOf(track)
        //clear on end
        var next: Any? = null
        if (trackIndex == queue.size - 1 && !loop) {
            queue.clear()
            //prepare queue and loop
        } else if (trackIndex == queue.size - 1 && loop) {
            rebuildQueue()
            next = queue.elementAt(0)
            if (track==next){
                next = null
            }
            queue.remove(queue.elementAt(trackIndex))
            //play next
        } else {
            next = queue.elementAt(trackIndex + 1)
            queue.remove(queue.elementAt(trackIndex))
        }
        if (next != null){
            if (next is Track) {
                commonPlayer.efPlayer.play(next)
//            } else if (next is AudioTrack) {
//                commonPlayer.lavaplayer.playTrack(next)
            }
        }
        val client = Bot.getClientInstance()
        val channel = client.getTextChannelById(updatesChannel)
        channel?.sendMessage("An error occurred during playback")?.queue(
            {},
            { Logging.logger.error("Failed to send playback failure message in ${channel.guild}") }
        )

    }

//    fun onTrackStuck(track: AudioTrack?) {
//        val trackIndex = queue.indexOf(track)
//        //clear on end
//        var next: Any? = null
//        if (trackIndex == queue.size - 1 && !loop) {
//            queue.clear()
//            //prepare queue and loop
//        } else if (trackIndex == queue.size - 1 && loop) {
//            rebuildQueue()
//            next = queue.elementAt(0)
//            queue.remove(queue.elementAt(trackIndex))
//            //play next
//        } else {
//            next = queue.elementAt(trackIndex + 1)
//            queue.remove(queue.elementAt(trackIndex))
//        }
//        if (next is Track) {
//            commonPlayer.efPlayer.play(next)
//        } else if (next is AudioTrack) {
//            commonPlayer.lavaplayer.playTrack(next)
//        }
//        val client = Bot.getClientInstance()
//        val channel = client.getTextChannelById(updatesChannel)
//        channel?.sendMessage("An error occurred during playback")?.queue(
//            {},
//            { Logging.logger.error("Failed to send playback failure message in ${channel.guild}") }
//        )
//
//    }
}