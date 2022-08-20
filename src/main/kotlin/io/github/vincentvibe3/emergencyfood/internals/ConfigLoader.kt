package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import kotlin.IllegalArgumentException
import kotlin.system.exitProcess

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
//        tasks.add { loadKTS(tempConfig) }
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
            when (it.key){
                "channel" -> Config.channel = it.value as Channel
                "token" -> Config.token = it.value as String
                "owner" -> Config.owner = it.value as String
                "exclusions" -> Config.exclusions = it.value as ArrayList<String>
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

    private fun jsonReadScope(tempConfig: HashMap<String, Any>, scope: Pair<String, JSONObject>) {
        val stringParams = arrayOf(
            "owner", "status", "prefix", "token", "testServer", "logflareUrl", "logflareKey", "envName", "supabaseUrl", "supabaseKey"
        )
        val arrayParams = arrayOf(
            "exclusions"
        )
        for (key in stringParams) {
            try {
                val value = scope.second.getString(key)
                if (value != "") {
                    updateSetting(key, value, tempConfig)
                } else if (required.contains(key) && scope.first != "Global" && !tempConfig.containsKey(key)) {
                    Logging.logger.warn("Could not load $key from botConfig.json in ${scope.first} scope")
                }
            } catch (e: JSONException) {
                if (required.contains(key) && scope.first != "Global" && !tempConfig.containsKey(key)) {
                    Logging.logger.warn("Could not load $key from botConfig.json in ${scope.first} scope")
                }
            }
        }
        for (key in arrayParams) {
            try {
                val paramArray = ArrayList<String>()
                val value = scope.second.getJSONArray(key)
                for (index in 0 until value.length()) {
                    val stringVal = value.optString(index)
                    paramArray.add(stringVal)
                }
                updateSetting(key, paramArray, tempConfig)
            } catch (e: JSONException) {
                if (required.contains(key) && scope.first != "Global" && !tempConfig.containsKey(key)) {
                    Logging.logger.warn("Could not load $key from botConfig.json in ${scope.first} scope")
                }
            }
        }
    }

    private fun loadJSON(tempConfig: HashMap<String, Any>) {
        Logging.logger.info("Loading botConfig.json...")
        lateinit var data: String
        try {
            data = loadFile(JSON)
        } catch (e: FileNotFoundException) {
            return
        }

        lateinit var jsonData: JSONObject
        try {
            jsonData = JSONObject(data)
        } catch (e: JSONException) {
            Logging.logger.warn("Invalid botConfig.json file. Trying config.bot.kts")
            return
        }

        lateinit var channelValue: Channel
        lateinit var channelName: String
        //load required
        try {
            channelName = jsonData.getString("Channel")
            channelValue = Channel.valueOf(channelName.uppercase())
            updateSetting("channel", channelValue, tempConfig)
        } catch (e: JSONException) {
            Logging.logger.warn("Channel could not be read from botConfig.json")
        } catch (e: IllegalArgumentException) {
            Logging.logger.warn("Invalid channel value found in botConfig.json")
        }

        try {
            val globalSettings = jsonData.getJSONObject("Global")
            jsonReadScope(tempConfig, Pair("Global", globalSettings))
            val scope = when (channelValue) {
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
        } catch (e: JSONException) {
            Logging.logger.warn("Global scope or specific scope was not found in botConfig.json")
        } catch (e: UninitializedPropertyAccessException) {
            return
        }
    }

    private fun loadKTS(tempConfig: HashMap<String, Any>) {
        val file = File(KTS)
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
        val exclusions = exclusionsValues?.replace("\"", "")?.split(" ") ?: ArrayList()
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