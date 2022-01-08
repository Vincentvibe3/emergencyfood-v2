package io.github.vincentvibe3.emergencyfood.utils.audio.efplayer

import com.github.Vincentvibe3.efplayer.core.EventListener
import com.github.Vincentvibe3.efplayer.core.Player
import com.github.Vincentvibe3.efplayer.core.Track
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Logging
import io.github.vincentvibe3.emergencyfood.utils.audio.common.QueueManager
import io.github.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
import kotlinx.coroutines.runBlocking

class PlaybackHandler(private val queueManager: QueueManager): EventListener() {

    override fun onLoadFailed() {
        Logging.logger.error("Failed to load a track")
        throw LoadFailedException()
    }

    override suspend fun onPlaylistLoaded(track: List<Track>, player: Player) {
        val initSize = queueManager.queue.size
        var successCount = 0
        track.forEach {
            if (queueManager.addToQueue(it)){
                successCount++
            }
        }
        if (successCount!=track.size){
            val client = Bot.getClientInstance()
            val channelId = queueManager.updatesChannel
            val channel = client.getTextChannelById(channelId)
            channel?.sendMessage("Failed to add ${track.size-successCount}, the rest was added")?.queue()
        }
        if (initSize == 0){
            val firstSong = queueManager.queue.peek()
            player.play(firstSong as Track)
        }
    }

    override fun onTrackDone(track: Track, player: Player, canStartNext: Boolean) {
        runBlocking {
            queueManager.onTrackEnd(track, canStartNext)
        }
    }

    override fun onTrackError(track: Track) {
        runBlocking {
            queueManager.onTrackException(track)
        }
    }

    override fun onTrackStart(track: Track, player: Player) {
        runBlocking {
            queueManager.onTrackStart(track)
        }
    }

    override suspend fun onTrackLoad(track: Track, player: Player) {
        queueManager.addToQueue(track)
        if (queueManager.queue.size == 1){
            val firstSong = queueManager.queue.peek()
            player.play(firstSong as Track)
        }
    }

}