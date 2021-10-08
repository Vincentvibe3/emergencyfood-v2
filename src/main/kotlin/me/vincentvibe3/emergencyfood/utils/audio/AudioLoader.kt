package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class AudioLoader(private val queueManager: QueueManager, private val player:AudioPlayer): AudioLoadResultHandler {

    // add loaded tracks to queue and play if queue was empty
    override fun trackLoaded(track: AudioTrack?) {
        if (track != null) {
            queueManager.addToQueue(track)
            if (queueManager.queue.size == 1){
                val firstSong = queueManager.queue.peek()
                player.playTrack(firstSong)
            }
        }
    }

    // add loaded tracks from playlist to queue and play if queue was empty
    override fun playlistLoaded(playlist: AudioPlaylist?) {
        if (queueManager.queue.size == 0){
            playlist?.tracks?.forEach { queueManager.addToQueue(it) }
            val firstSong = queueManager.queue.peek()
            player.playTrack(firstSong)
        } else {
            playlist?.tracks?.forEach { queueManager.addToQueue(it) }
        }
    }

    override fun noMatches() {
        println("nomatch")
        throw SongNotFoundException()
    }

    override fun loadFailed(exception: FriendlyException?) {
        println("failed")
        throw LoadFailedException()
    }
}