package io.github.vincentvibe3.emergencyfood.utils.audio.player.extractors

import io.github.vincentvibe3.emergencyfood.utils.audio.player.Track

abstract class Extractor {

    abstract suspend fun getStream(url:String):String?

    abstract suspend fun getTrack(url: String): Track?

    open fun getPlaylistTracks():List<Track>{
        return ArrayList()
    }

}