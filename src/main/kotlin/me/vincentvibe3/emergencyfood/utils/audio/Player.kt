package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer

class Player {
    private val playerManager = DefaultAudioPlayerManager()
    private val player: AudioPlayer = playerManager.createPlayer()
    private val queueManager = QueueManager()
    private val handler = AudioHandler(player)

    fun setupPlayer(){
        playerManager.configuration.setFrameBufferFactory{ bufferDuration, format, stopping -> NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping)}
        AudioSourceManagers.registerRemoteSources(playerManager)
        player.addListener(queueManager)
    }

    fun getAudioHandler():AudioHandler{
        return handler
    }

    fun play(track:String){
        playerManager.loadItemOrdered(player,  track , AudioLoader(queueManager, player))
    }

    fun getPlayingSong(): String{
        return player.playingTrack.info.title
    }

    fun skip(){
        val queue = queueManager.queue
        val currentIndex = queue.indexOf(player.playingTrack)
        val nextTrack = queue.elementAt(currentIndex+1)
        player.playTrack(nextTrack)
    }

    fun isQueueEmpty(): Boolean{
        return queueManager.queue.size == 0
    }

    fun isLastSong():Boolean{
        val queue = queueManager.queue
        val currentIndex = queue.indexOf(player.playingTrack)
        return currentIndex == queue.size-1
    }

    fun stop(){
        player.stopTrack()
        queueManager.queue.clear()
    }

    fun resume(){
        player.isPaused = false
    }

    fun pause(){
        player.isPaused = true
    }

    fun isPaused(): Boolean{
        return player.isPaused
    }

    fun isPlaying(): Boolean{
        return player.playingTrack != null
    }

}