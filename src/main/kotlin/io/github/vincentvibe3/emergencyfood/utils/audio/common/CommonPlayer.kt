package io.github.vincentvibe3.emergencyfood.utils.audio.common

import com.github.Vincentvibe3.efplayer.core.EventListener
import com.github.Vincentvibe3.efplayer.core.Player
import com.github.Vincentvibe3.efplayer.core.Track
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import io.github.vincentvibe3.emergencyfood.utils.audio.efplayer.AudioHandlerEf
import io.github.vincentvibe3.emergencyfood.utils.audio.efplayer.PlaybackHandler
import io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer.AudioHandler
import io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer.AudioLoader
import io.github.vincentvibe3.emergencyfood.utils.audio.lavaplayer.EventAdapter
import io.github.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
import io.github.vincentvibe3.emergencyfood.utils.exceptions.QueueAddException
import io.github.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.math.BigInteger
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

class CommonPlayer(val guild:String) {
    private val playerManager = DefaultAudioPlayerManager()
    private val queueManager = QueueManager(this)
    val lavaplayer: AudioPlayer = playerManager.createPlayer()
    private val efListener:EventListener = PlaybackHandler(queueManager)
    val efPlayer: Player= Player(efListener)
    private val eventAdapter = EventAdapter(queueManager)
    private val efHandler = AudioHandlerEf(efPlayer)
    private val lavahandler = AudioHandler(lavaplayer)
    private val defaultPlayer = "ef"

    val loadQueue = LinkedBlockingQueue<LoadResult>()

    val addOrder = LinkedBlockingQueue<BigInteger>()

    fun setupPlayer(){
        playerManager.configuration.setFrameBufferFactory{ bufferDuration, format, stopping -> NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping)}
        AudioSourceManagers.registerRemoteSources(playerManager)
        lavaplayer.addListener(eventAdapter)
    }

    fun toggleLoop(): Boolean{
        queueManager.loop = !queueManager.loop
        return queueManager.loop
    }

    fun getLoop():Boolean{
        return queueManager.loop
    }

    fun setUpdateChannel(channel:String){
        queueManager.updatesChannel = channel
    }

    fun getUpdateChannel():String{
        return queueManager.updatesChannel
    }

    fun getAudioHandler(): AudioSendHandler {
        return if (getCurrentSong() is AudioTrack){
            lavahandler
        } else if (getCurrentSong() is Track){
            efHandler
        } else{
            efHandler
        }
    }

    fun getAnnouncementChannel():String {
        return queueManager.updatesChannel
    }

    fun play(track:String){
        if (queueManager.queue.size == Int.MAX_VALUE){
            throw QueueAddException()
        }
        try {
            if (defaultPlayer=="ef"){
                efPlayer.load(track)
            } else {
                val load = playerManager.loadItemOrdered(lavaplayer,  track, AudioLoader(queueManager, lavaplayer))
                load.get()
            }
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

    fun getQueue(): BlockingQueue<Any>{
        return queueManager.queue
    }

    fun getCurrentSong(): Any? {
        val lpTrack =lavaplayer.playingTrack
        val efTrack = efPlayer.currentTrack
        return efTrack ?: lpTrack
    }

    fun getCurrentSongUrl(): String{
        val currentSong = getCurrentSong()
        return if (currentSong is Track){
            currentSong.url
        } else if (currentSong is AudioTrack){
            currentSong.info.uri
        } else {
            ""
        }
    }

    fun getCurrentSongTitle(): String{
        val currentSong = getCurrentSong()
        return if (currentSong is Track){
            currentSong.title ?: ""
        } else if (currentSong is AudioTrack){
            currentSong.info.title
        } else {
            ""
        }
    }

    fun getLastSongUrl():String{
        val last = queueManager.queue.last()
        return if (last is AudioTrack){
            last.info.uri
        } else if (last is Track){
            last.url
        } else {
            ""
        }


    }

    fun getLastSongTitle():String{
        val last = queueManager.queue.last()
        return if (last is AudioTrack){
            last.info.title
        } else if (last is Track){
            last.title ?: ""
        } else {
            ""
        }
    }

    fun getSongUrl(pos:Int):String{
        val last = queueManager.queue.elementAt(pos)
        return if (last is AudioTrack){
            last.info.uri
        } else if (last is Track){
            last.url
        } else {
            ""
        }


    }

    fun getSongTitle(pos: Int):String{
        val last = queueManager.queue.elementAt(pos)
        return if (last is AudioTrack){
            last.info.title
        } else if (last is Track){
            last.title ?: ""
        } else {
            ""
        }
    }

    fun getCurrentSongIndex():Int{
        val currentSong = getCurrentSong()
        return queueManager.queue.indexOf(currentSong)
    }

    fun skip(){
        if (isLastSong()&&getLoop()) {
            queueManager.rebuildQueue()
            val queue = queueManager.queue
            val next = queue.elementAt(0)
            if (next is Track) {
                efPlayer.play(next)
            } else if (next is AudioTrack) {
                lavaplayer.playTrack(next)
            }
        } else if (isLastSong()&&!getLoop()){
            val queue = queueManager.queue
            val currentTrack = queue.last
            if (currentTrack is Track){
                efPlayer.stop()
            } else if (currentTrack is AudioTrack){
                lavaplayer.stopTrack()
            }
            queueManager.queue.clear()
        } else {
            val queue = queueManager.queue
            val currentIndex = getCurrentSongIndex()
            val next = queue.elementAt(currentIndex+1)
            if (next is Track){
                efPlayer.play(next)
            } else if (next is AudioTrack){
                lavaplayer.playTrack(next)
            }
        }
    }

    fun isQueueEmpty(): Boolean{
        return queueManager.queue.size == 0
    }

    fun isLastSong():Boolean{
        val queue = queueManager.queue
        val currentIndex = queue.indexOf(lavaplayer.playingTrack)
        return currentIndex == queue.size-1
    }

    fun clear(){
        queueManager.queue.clear()
        lavaplayer.stopTrack()
        efPlayer.stop()
    }

    fun stop(){
        efPlayer.stop()
        lavaplayer.stopTrack()
    }

    fun resume(){
        efPlayer.resume()
        lavaplayer.isPaused = false
    }

    fun pause(){
        efPlayer.pause()
        lavaplayer.isPaused = true
    }

    fun isPaused(): Boolean{
        return if (getCurrentSong() is AudioTrack){
            lavaplayer.isPaused
        } else if (getCurrentSong() is Track){
            efPlayer.paused
        } else{
            false
        }
    }

    fun isPlaying(): Boolean{
        return lavaplayer.playingTrack != null || efPlayer.currentTrack != null
    }

    fun shuffle(){
        val newQueue: BlockingQueue<Any> = LinkedBlockingQueue()
        val nowPlaying = getCurrentSong()
        queueManager.queue.filter { it!=nowPlaying }.forEach{
            if (it is AudioTrack){
                newQueue.offer(it.makeClone())
            } else if (it is Track){
                newQueue.offer(it)
            }
        }
        queueManager.queue = LinkedBlockingDeque(newQueue.shuffled())
        if (nowPlaying!=null){
            queueManager.queue.offerFirst(nowPlaying)
        }
    }

}