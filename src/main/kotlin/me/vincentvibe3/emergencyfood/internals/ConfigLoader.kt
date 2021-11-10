package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.utils.Logging
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.InvocationTargetException
import kotlin.IllegalArgumentException
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

object ConfigLoader {

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
        val tempConfig = HashMap<String, Any>()
        val tasks = ArrayList< (HashMap<String, Any>) -> Unit >()
        tasks.add { loadJSON(tempConfig) }
//        tasks.add { loadKTS(tempConfig) }
        tasks.add { loadENV(tempConfig) }
        tasks.forEach {
            if (!checkSetting()){
                it.invoke(tempConfig)
            }
        }
        applySetting(tempConfig)
    }

    private fun checkSetting():Boolean{
        this::class.declaredMemberProperties.forEach {
            if (it.isLateinit && it.javaField?.get(this) == null){
                println(it.name)
                try {
                    it.takeIf { property -> property is KMutableProperty<*> }
                        .let { property -> property as KMutableProperty<*> }
                        .getter.call(this)
                } catch (e:InvocationTargetException){
                    return false
                }

            }
        }
        return true
    }

    private fun applySetting(tempConfig: HashMap<String, Any> ){
        this::class.declaredMemberProperties.forEach {
            if (it.isLateinit && it.javaField?.get(this) == null){
                println(it.name)
                it.takeIf { property -> property is KMutableProperty<*> }
                .let { property -> property as KMutableProperty<*> }
                    .setter.call(this, tempConfig[it.name])
            }
        }
    }

    private fun loadFile(path:String):String{
        val file = File(path)
        return file.bufferedReader()
            .lines()
            .toArray()
            .joinToString("\n")
    }

    private fun updateSetting(key:String, value:Any, tempConfig: HashMap<String, Any>){
        if (tempConfig[key] == null){
            tempConfig[key] = value
        }
    }

    private fun jsonReadScope(tempConfig: HashMap<String, Any>, scope:Pair<String, JSONObject>){
        val stringParams = arrayOf(
            "owner", "status", "prefix", "token"
        )
        for (key in stringParams){
            try {
                val value = scope.second.getString(key)
                if (value!=""){
                    updateSetting(key, value, tempConfig)
                }else{
                    Logging.logger.warn("Could not load $key from botConfig.json in ${scope.first} scope")
                }
            } catch (e:JSONException){
                Logging.logger.warn("Could not load $key from botConfig.json in ${scope.first} scope")
            }
        }
    }

    private fun loadJSON(tempConfig:HashMap<String, Any>){
        Logging.logger.info("Loading botConfig.json...")
        lateinit var data:String
        try {
            data = loadFile(JSON)
        } catch (e:FileNotFoundException){
            return
        }

        lateinit var jsonData:JSONObject
        try {
            jsonData = JSONObject(data)
        } catch (e:JSONException){
            Logging.logger.warn("Invalid botConfig.json file. Trying config.bot.kts")
            return
        }

        lateinit var channelValue:Channel
        lateinit var channelName:String
        //load required
        try {
            channelName = jsonData.getString("Channel")
            channelValue = Channel.valueOf(channelName.uppercase())
            updateSetting("channel", channelValue, tempConfig)
        } catch (e:JSONException){
            Logging.logger.warn("Channel could not be read from botConfig.json")
        } catch (e:IllegalArgumentException){
            Logging.logger.warn("Invalid channel value found in botConfig.json")
        }

        try{
            val globalSettings = jsonData.getJSONObject("Global")
            jsonReadScope(tempConfig, Pair("Global", globalSettings))
            val scope = when (channelValue){
                Channel.STABLE -> {
                    jsonData.getJSONObject("Stable")
                }
                Channel.BETA -> {
                    jsonData.getJSONObject("Beta")
                }
                Channel.LOCAL -> {
                    jsonData.getJSONObject("Local")
                }
            }
            jsonReadScope(tempConfig, Pair(channelName, scope))
        } catch (e:JSONException){
            Logging.logger.warn("Global scope or specific scope was not found in botConfig.json")
        } catch (e:UninitializedPropertyAccessException){
            return
        }
    }

    private fun loadKTS(tempConfig:HashMap<String, Any>){
        val file = File(KTS)
    }

    private fun loadENV(tempConfig:HashMap<String, Any>){
        Logging.logger.info("Loading config from Env")
        val channelValue = System.getenv("CHANNEL")
        val channelType = if (channelValue==null){
            Channel.STABLE
        } else{
            try {
                Channel.valueOf(channelValue)
            } catch (e:IllegalArgumentException){
                Channel.STABLE
            }
        }
        updateSetting("channel", channelType, tempConfig)
        val tempToken = when (channelType) {
            Channel.STABLE -> {
                System.getenv("TOKEN")
            }
            Channel.BETA -> {
                System.getenv("TOKEN_BETA")
            }
            Channel.LOCAL -> {
                System.getenv("TOKEN")
            }
        }
        val tempOwner = System.getenv("BOT_OWNER")
        updateSetting("token", tempToken, tempConfig)
        if (tempOwner!=null){
            updateSetting("owner", tempOwner, tempConfig)
        }
    }
}