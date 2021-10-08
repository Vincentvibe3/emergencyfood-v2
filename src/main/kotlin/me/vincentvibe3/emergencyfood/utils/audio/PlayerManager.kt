package me.vincentvibe3.emergencyfood.utils.audio

object PlayerManager {

    private val activeGuilds = HashMap<String, Player>()

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

}