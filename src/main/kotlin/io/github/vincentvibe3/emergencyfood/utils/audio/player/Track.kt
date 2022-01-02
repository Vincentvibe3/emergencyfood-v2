package io.github.vincentvibe3.emergencyfood.utils.audio.player

import io.github.vincentvibe3.emergencyfood.utils.audio.player.extractors.Extractor

class Track(val url:String, val extractor:Extractor, val title:String, val duration:Long) {

    suspend fun getStream():String?{
        return extractor.getStream(url)
    }

}