package io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import io.github.vincentvibe3.emergencyfood.utils.audio.common.QueueManager
import kotlinx.coroutines.runBlocking

class EventAdapter(private val queueManager: QueueManager) : AudioEventAdapter() {

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (endReason != null) {
            if (player != null && track != null) {
                runBlocking {
                    queueManager.onTrackEnd(track, endReason.mayStartNext)
                }
            }
        }
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        if (track != null) {
            queueManager.onTrackStart(track)
        }
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        runBlocking {
            if (track != null) {
                queueManager.onTrackException(track)
            }
        }
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        runBlocking {
            queueManager.onTrackStuck(track)
        }

    }
}