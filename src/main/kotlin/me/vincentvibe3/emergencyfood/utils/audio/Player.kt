package me.vincentvibe3.emergencyfood.utils.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import me.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
import me.vincentvibe3.emergencyfood.utils.exceptions.QueueAddException
import me.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

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

    fun toggleLoop(): Boolean{
        queueManager.loop = !queueManager.loop
        return queueManager.loop
    }

    fun getLoop():Boolean{
        return  queueManager.loop
    }

    fun setUpdateChannel(channel:String){
        queueManager.updatesChannel = channel
    }

    fun getAudioHandler():AudioHandler{
        return handler
    }

    fun play(track:String){
        if (queueManager.queue.size == Int.MAX_VALUE){
            throw QueueAddException()
        }
        val load = playerManager.loadItemOrdered(player,  track, AudioLoader(queueManager, player))
        try {
            load.get()
        } catch (e:ExecutionException) {
            when (e.cause?.javaClass) {
                LoadFailedException::class.java -> {
                    throw LoadFailedException()
                }
                SongNotFoundException::class.java -> {
                    throw SongNotFoundException()
                }
            }
        }

    }

    fun getQueue(): BlockingQueue<AudioTrack>{
        return queueManager.queue
    }

    fun getCurrentSong(): AudioTrack{
        return player.playingTrack
    }

    fun getCurrentSongUrl(): String{
        return player.playingTrack.info.uri
    }

    fun getCurrentSongTitle(): String{
        return player.playingTrack.info.title
    }

    fun getLastSongUrl():String{
        return queueManager.queue.last().info.uri
    }

    fun getLastSongTitle():String{
        return queueManager.queue.last().info.title
    }

    fun getCurrentSongIndex():Int{
        return queueManager.queue.indexOf(player.playingTrack)
    }

    fun skip(){
        if (isLastSong()&&getLoop()){
            queueManager.rebuildQueue()
            val queue = queueManager.queue
            player.playTrack(queue.elementAt(0))
        } else {
            val queue = queueManager.queue
            val currentIndex = queue.indexOf(player.playingTrack)
            val nextTrack = queue.elementAt(currentIndex+1)
            player.playTrack(nextTrack)
        }
    }

    fun isQueueEmpty(): Boolean{
        return queueManager.queue.size == 0
    }

    fun isLastSong():Boolean{
        val queue = queueManager.queue
        val currentIndex = queue.indexOf(player.playingTrack)
        return currentIndex == queue.size-1
    }

    fun clear(){
        player.stopTrack()
        queueManager.queue.clear()
    }

    fun stop(){
        player.stopTrack()
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

    fun shuffle(){
        val newQueue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
        val nowPlaying = getCurrentSong()
        queueManager.queue.filter { it!=nowPlaying }.forEach{
            newQueue.offer(it.makeClone())
        }
        queueManager.queue = LinkedBlockingDeque(newQueue.shuffled())
        queueManager.queue.offerFirst(nowPlaying)
    }

}