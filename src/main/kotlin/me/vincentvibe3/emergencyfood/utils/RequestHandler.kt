package me.vincentvibe3.emergencyfood.utils

import com.github.kittinunf.fuel.httpGet
import me.vincentvibe3.emergencyfood.utils.audio.SongSearch
import java.net.URI

object RequestHandler {

    private val domains = HashMap<String, Long>()
    private val rateLimits = HashMap<String, Long>()

    fun get(originalUrl: String):String{
        val host = URI(originalUrl).host
        val currentCount = domains[host]
        if (currentCount != null){
            domains[host] = currentCount + 1
        } else {
            domains[host] = 0
        }
        val httpAsync = host.httpGet()
            .responseString { request, response, result ->
                val (responseText, _) = result
                if (responseText != null && response.statusCode == 200){

                } else {

                }
            }
        if (rateLimits.getOrDefault()){

        }
        httpAsync.join()

        return host
    }

}