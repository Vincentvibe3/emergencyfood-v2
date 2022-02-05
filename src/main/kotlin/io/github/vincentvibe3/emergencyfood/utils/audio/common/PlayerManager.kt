package io.github.vincentvibe3.emergencyfood.utils.audio.common

import kotlinx.coroutines.delay
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Templates
import net.dv8tion.jda.api.MessageBuilder

object PlayerManager {

    private val activeGuilds = HashMap<String, CommonPlayer>()
    private val checkForCleanup = HashMap<String, Long>()

    //fetches the player for a guild and creates a new one if none is found
    fun getPlayer(guild:String): CommonPlayer {
        val currentPlayer = activeGuilds[guild]
        return if (currentPlayer != null){
            currentPlayer
        } else {
            val newPlayer = CommonPlayer(guild)
            newPlayer.setupPlayer()
            activeGuilds[guild] = newPlayer
            newPlayer
        }
    }

    //removes a player from a guild
    fun removePlayer(guild: String){
        activeGuilds[guild]?.stop()
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

    fun isSetForCleanup(guildId: String):Boolean{
        return checkForCleanup.contains(guildId)
    }

    private fun cleanUp(guildId: String){
        val client = Bot.getClientInstance()
        val guild = client.getGuildById(guildId)
        val updatedPlayer = getPlayer(guildId)
        val messageChannel = updatedPlayer.getAnnouncementChannel()
        val embed = Templates.getMusicEmbed()
            .setTitle("Disconnected due to inactivity")
            .build()
        val message = MessageBuilder()
            .setEmbeds(embed)
            .build()
        client.getTextChannelById(messageChannel)?.sendMessage(message)?.queue()
        removePlayer(guildId)
        guild?.audioManager?.closeAudioConnection()
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