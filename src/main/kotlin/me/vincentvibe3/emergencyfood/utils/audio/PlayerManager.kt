package me.vincentvibe3.emergencyfood.utils.audio

object PlayerManager {

    private val activeGuilds = HashMap<String, Player>()

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

    fun removePlayer(guild: String){
        activeGuilds.remove(guild)
    }

}