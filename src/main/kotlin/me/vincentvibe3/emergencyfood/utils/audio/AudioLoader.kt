package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.vincentvibe3.emergencyfood.core.Bot

class AudioLoader(private val queueManager: QueueManager, private val player:AudioPlayer): AudioLoadResultHandler {

    // add loaded tracks to queue and play if queue was empty
    override fun trackLoaded(track: AudioTrack) {
        queueManager.addToQueue(track)
        if (queueManager.queue.size == 1){
            val firstSong = queueManager.queue.peek()
            player.playTrack(firstSong)
        }
    }

    // add loaded tracks from playlist to queue and play if queue was empty
    override fun playlistLoaded(playlist: AudioPlaylist) {
        val initSize = queueManager.queue.size
        var successCount = 0
        playlist.tracks.forEach {
            if (queueManager.addToQueue(it)){
                successCount++
            }
        }
        if (successCount!=playlist.tracks.size){
            val client = Bot.getClientInstance()
            val channelId = queueManager.updatesChannel
            val channel = client.getTextChannelById(channelId)
            channel?.sendMessage("Failed to add ${playlist.tracks.size-successCount}, the rest was added")?.queue()
        }
        if (initSize == 0){
            val firstSong = queueManager.queue.peek()
            player.playTrack(firstSong)
        }
    }

    override fun noMatches() {
        throw SongNotFoundException()
    }

    override fun loadFailed(exception: FriendlyException?) {
        throw LoadFailedException()
    }
}