package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.serialization.ConfigData
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

object ConfigLoader {

    /**
    enum representing environments
    in which the bot may
     **/
    enum class Channel {
        BETA, STABLE, LOCAL
    }

    private const val JSON = "botConfig.json"
    private val required = arrayOf("channel", "token", "owner", "supabaseUrl", "supabaseKey")
    private val requiredSet = hashMapOf(
        "channel" to false,
        "token" to false,
        "owner" to false,
        "supabaseUrl" to true,
        "supabaseKey" to true
    )

    /** preferred methods of config
    1. json
    2. kotlin script
    3. environment variables
     **/
    fun load() {
        val tempConfig = HashMap<String, Any>()
        val tasks = ArrayList<(HashMap<String, Any>) -> Unit>()
        tasks.add { loadJSON(tempConfig) }
        tasks.add { loadENV(tempConfig) }
        tasks.forEach {
            if (!checkSetting().second) {
                it.invoke(tempConfig)
                applySetting(tempConfig)
            }
        }
        val checkResult = checkSetting()
        if (!checkResult.second) {
            Logging.logger.error("Unable to load required setting (${checkResult.first}) from config")
            exitProcess(1)
        }
    }

    private fun checkSetting(): Pair<String, Boolean> {
        requiredSet.forEach {
            if (!it.value) {
                return Pair(it.key, false)
            }
        }
        return Pair("ok", true)
    }

    private fun applySetting(tempConfig: HashMap<String, Any> ){
        tempConfig.forEach {
            @Suppress("UNCHECKED_CAST")
            when (it.key){
                "channel" -> Config.channel = it.value as Channel
                "token" -> Config.token = it.value as String
                "owner" -> Config.owner = it.value as String
                "exclusions" -> Config.exclusions = it.value as List<String>
                "prefix" -> Config.prefix = it.value as String
                "status" -> Config.status = it.value as String
                "testServer" -> Config.testServer = it.value as String
                "logflareUrl" -> Config.logflareUrl = it.value as String
                "logflareKey" -> Config.logflareKey = it.value as String
                "envName" -> Config.envName = it.value as String
                "supabaseUrl" -> Config.supabaseUrl = it.value as String
                "supabaseKey" -> Config.supabaseKey = it.value as String
            }
        }
    }

    private fun loadFile(path: String): String {
        val file = File(path)
        return file.bufferedReader()
            .lines()
            .toArray()
            .joinToString("\n")
    }

    private fun updateSetting(key:String, value:Any, tempConfig: HashMap<String, Any>){
        if (tempConfig[key] == null){
            if (requiredSet.containsKey(key)){
                requiredSet[key] = true
            }
            tempConfig[key] = value
        }
    }

    private fun jsonReadScope(tempConfig: HashMap<String, Any>, scope: Pair<String, ConfigData.ConfigScopeData>) {
        val data = scope.second
        val scopeName = scope.first
        val stringParams = hashMapOf(
            "owner" to data.owner,
            "status" to data.status,
            "prefix" to data.prefix,
            "token" to data.token,
            "testServer" to data.testServer,
            "logflareUrl" to data.logflareUrl,
            "logflareKey" to data.logflareKey,
            "envName" to data.envName,
            "supabaseUrl" to data.supabaseUrl,
            "supabaseKey" to data.supabaseKey,
            "exclusions" to data.exclusions
        )

        for ((key, value) in stringParams){
            if (value is String&& value.isNotBlank()) {
                updateSetting(key, value, tempConfig)
            } else if (value is List<*> && value.isNotEmpty()){
                updateSetting(key, value, tempConfig)
            } else if (required.contains(key)&&scopeName=="Global"&&!tempConfig.containsKey(key)) {
                Logging.logger.warn("Could not load $key from botConfig.json in $scopeName scope")
            }
        }
    }

    private fun loadJSON(tempConfig: HashMap<String, Any>) {
        Logging.logger.info("Loading botConfig.json...")
        val data = try {
            loadFile(JSON)
        } catch (e: FileNotFoundException) {
            return
        }

        val jsonData = try {
            Json.decodeFromString<ConfigData>(data)
        } catch (e:java.lang.IllegalArgumentException) {
            Logging.logger.warn("Invalid botConfig.json file. Trying environment variables")
            return
        }

        val channelName = jsonData.Channel
        val globalSettings = jsonData.Global
        if (globalSettings!=null){
            jsonReadScope(tempConfig, Pair("Global", globalSettings))
        } else {
            Logging.logger.warn("Global scope was not found in botConfig.json")
        }
        if (channelName!=null){
            val channelValue = Channel.valueOf(channelName.uppercase())
            updateSetting("channel", channelValue, tempConfig)
            val scope = when (channelValue) {
                Channel.STABLE -> {
                    jsonData.Stable
                }
                Channel.BETA -> {
                    jsonData.Beta
                }
                Channel.LOCAL -> {
                    jsonData.Local
                }
            }
            if (scope!=null){
                jsonReadScope(tempConfig, Pair(channelName, scope))
            }
        } else {
            Logging.logger.warn("Channel could not be read from botConfig.json, Channel settings will be skipped")
        }
    }

    private fun loadENV(tempConfig: HashMap<String, Any>) {
        Logging.logger.info("Loading config from Env")
        val channelValue = System.getenv("CHANNEL")
        val channelType = if (channelValue == null) {
            Channel.STABLE
        } else {
            try {
                Channel.valueOf(channelValue)
            } catch (e: IllegalArgumentException) {
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
        val tempTestServer = System.getenv("TEST_SERVER")
        updateSetting("token", tempToken, tempConfig)
        val exclusionsValues = System.getenv("EXCLUSIONS")
        //fix issue on empty or one element
        val exclusions = exclusionsValues?.replace("\"", "")?.split(" ") ?: listOf()
        val tempLogflareUrl = System.getenv("LOGFLARE_URL")
        val tempLogflareKey = System.getenv("LOGFLARE_KEY")
        val tempEnvName = System.getenv("ENV_NAME")
        val tempSupabaseUrl = System.getenv("SUPABASE_URL")
        if (tempSupabaseUrl != null){
            updateSetting("supabaseUrl", tempSupabaseUrl, tempConfig)
        }
        val tempSupabaseKey = System.getenv("SUPABASE_KEY")
        if (tempSupabaseKey != null){
            updateSetting("supabaseKey", tempSupabaseKey, tempConfig)
        }
        if (tempEnvName != null){
            updateSetting("envName", tempEnvName, tempConfig)
        }
        if (tempLogflareUrl != null){
            updateSetting("logflareUrl", tempLogflareUrl, tempConfig)
        }
        if (tempLogflareKey != null){
            updateSetting("logflareKey", tempLogflareKey, tempConfig)
        }
        if (exclusions.isNotEmpty()){
            updateSetting("exclusions", exclusions, tempConfig)
        }
        if (tempOwner != null) {
            updateSetting("owner", tempOwner, tempConfig)
        }
        if (tempTestServer != null) {
            updateSetting("testServer", tempTestServer, tempConfig)
        }
    }
}