package io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer
//
//import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
//import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
//import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
//import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
//import com.sedmelluq.discord.lavaplayer.track.AudioTrack
//import io.github.vincentvibe3.emergencyfood.core.Bot
//import io.github.vincentvibe3.emergencyfood.utils.audio.common.QueueManager
//import io.github.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
//import io.github.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
//import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
//
//class AudioLoader(private val queueManager: QueueManager, private val player: AudioPlayer) : AudioLoadResultHandler {
//
//    // add loaded tracks to queue and play if queue was empty
//    override fun trackLoaded(track: AudioTrack) {
//        queueManager.addToQueue(track, false)
//        if (queueManager.queue.size == 1) {
//            val firstSong = queueManager.queue.peek()
//            player.playTrack(firstSong as AudioTrack?)
//        }
//    }
//
//    // add loaded tracks from playlist to queue and play if queue was empty
//    override fun playlistLoaded(playlist: AudioPlaylist) {
//        val initSize = queueManager.queue.size
//        var successCount = 0
//        playlist.tracks.forEach {
//            if (queueManager.addToQueue(it, true)) {
//                successCount++
//            }
//        }
//        if (successCount != playlist.tracks.size) {
//            val client = Bot.getClientInstance()
//            val channelId = queueManager.updatesChannel
//            val channel = client.getTextChannelById(channelId)
//            channel?.sendMessage("Failed to add ${playlist.tracks.size - successCount}, the rest was added")?.queue()
//        }
//        queueManager.playlistLoadedMessage(successCount, playlist.tracks.first().identifier)
//        if (initSize == 0) {
//            val firstSong = queueManager.queue.peek()
//            player.playTrack(firstSong as AudioTrack?)
//        }
//    }
//
//    override fun noMatches() {
//        throw SongNotFoundException()
//    }
//
//    override fun loadFailed(exception: FriendlyException?) {
//        Logging.logger.error(exception?.message)
//        throw LoadFailedException()
//    }
//}