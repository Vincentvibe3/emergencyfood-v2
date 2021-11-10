package me.vincentvibe3.emergencyfood.internals

import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.IllegalArgumentException

object Config {

    /**
        enum representing environments
        in which the bot may
     **/
    enum class Channel {
        BETA, STABLE, LOCAL
    }

    private const val KTS = "config.bot.kts"
    private const val JSON = "botConfig.json"
    lateinit var channel: Channel
    lateinit var token:String

    /** preferred methods of config
        1. json
        2. kotlin script
        3. environment variables
    **/
    fun load(){
        if (File(JSON).exists()){
            loadJSON()
        } else if (File(KTS).exists()){
            loadKTS()
        } else {
            loadENV()
        }
    }

    private fun loadJSON(){
        val file = File(JSON)
        var data = ""
        file.bufferedReader().lines().forEach{ data+="$it \n" }
        try {
            val jsonData = JSONObject(data)
            val channelName = jsonData.getString("Channel")
            channel = Channel.valueOf(channelName.uppercase())
            when (channel){
                Channel.STABLE -> {

                }
            }
        } catch (e:JSONException){
            throw e
        } catch (e:IllegalArgumentException){
            throw e
        }


    }

    private fun loadKTS(){
        val file = File(KTS)
    }

    private fun loadENV(){
        when (channel) {
            Channel.STABLE -> {
                token = System.getenv("TOKEN")
            }
            Channel.BETA -> {
                token = System.getenv("TOKEN_BETA")
            }
            Channel.LOCAL -> {
                token = System.getenv("TOKEN")
            }
        }
    }
}