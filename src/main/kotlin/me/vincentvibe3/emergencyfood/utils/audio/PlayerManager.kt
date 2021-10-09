package me.vincentvibe3.emergencyfood.utils.audio

import kotlinx.coroutines.delay
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.MessageBuilder

object PlayerManager {

    private val activeGuilds = HashMap<String, Player>()
    private val checkForCleanup = HashMap<String, Long>()

    //fetches the player for a guild and creates a new onw if none is found
    fun getPlayer(guild:String): Player? {
        return if (activeGuilds[guild] != null){
            activeGuilds[guild]
        } else {
            val newPlayer = Player()
            newPlayer.setupPlayer()
            activeGuilds[guild] = newPlayer
            newPlayer
        }
    }

    //removes a player from a guild
    fun removePlayer(guild: String){
        activeGuilds.remove(guild)
    }

    suspend fun startCleanupLoop(){
        while (true) {
            if (checkForCleanup.isEmpty()){
                delay(30000L)
            } else {
                val checkTime = checkForCleanup.values.first()
                val guild = checkForCleanup.keys.first()
                val currentTime = System.currentTimeMillis()
                if (currentTime>=checkTime){
                    cleanUp(guild)
                } else {
                    delay(checkTime-currentTime)
                    if (checkForCleanup.keys.first()==guild){
                        cleanUp(guild)
                    }
                }
            }
        }
    }

    private fun cleanUp(guildId: String){
        val client = Bot.getClientInstance()
        val guild = client.getGuildById(guildId)
        val updatedPlayer = getPlayer(guildId)
        if (updatedPlayer != null) {
            val messageChannel = updatedPlayer.getAnnouncementChannel()
            val embed = Templates.getMusicEmbed()
                .setTitle("Disconnected to due to inactivity")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            client.getTextChannelById(messageChannel)?.sendMessage(message)?.queue()
            removePlayer(guildId)
            guild?.audioManager?.closeAudioConnection()
        }
        unsetForCleanup(guildId)
    }

    fun setForCleanup(guild: String){
        val currentTime = System.currentTimeMillis()
        val timeout = 5
        val checkTime = currentTime+(timeout*60*1000)
        checkForCleanup[guild] = checkTime
    }

    fun unsetForCleanup(guild: String){
        checkForCleanup.remove(guild)
    }

}