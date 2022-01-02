package io.github.vincentvibe3.emergencyfood.utils.audio.player

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import io.github.vincentvibe3.emergencyfood.utils.audio.SongSearch
import io.github.vincentvibe3.emergencyfood.utils.audio.player.extractors.Youtube
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class Queue {

    var queue: BlockingDeque<Track> = LinkedBlockingDeque()
    var loop = false

    // throws LoadFailedException if a track or playlist fails to be put into the queue
    suspend fun getTracks(query:String){
        if (query.startsWith("https://")||query.startsWith("http://")){
            if (query.startsWith("https://www.youtube.com/playlist?list=")){
                //youtube playlist extractor
                val tracks = Youtube.getPlaylistTracks(query)
                if (tracks.isEmpty()){
                    throw LoadFailedException()
                } else {
                    for (track in tracks) {
                        queue.offer(track)
                    }
                }
            } else if (query.startsWith("https://www.youtube.com/watch?v=")){
                //youtube single track extractor
                val track = Youtube.getTrack(query)
                if (track == null){
                    throw LoadFailedException()
                } else {
                    queue.offer(track)
                }
            }
        }else{
            //search youtube
            val link = SongSearch.getSong(query)
            val track = Youtube.getTrack(query)
            if (track == null){
                throw LoadFailedException()
            } else {
                queue.offer(track)
            }
        }
    }

}