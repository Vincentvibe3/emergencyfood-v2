package io.github.vincentvibe3.emergencyfood.internals

object MessageResponseManager {

    private val pendingResponses = HashMap<String, HashMap<String, ArrayList<MessageResponse>>>()

    fun add(response: MessageResponse){
        val user = response.user
        val channel  = response.channel
        val channelResponses = pendingResponses.getOrPut(channel) { HashMap() }
        val responseList = channelResponses.getOrPut(user){ ArrayList() }
        responseList.add(response)
    }

    fun get(user:String, channel:String):MessageResponse?{
        val channelResponses = pendingResponses.getOrDefault(channel, null)
        return if (channelResponses != null){
            channelResponses[user]?.firstOrNull()
        } else {
            null
        }
    }

    fun remove(response: MessageResponse) {
        val user = response.user
        val channel = response.channel
        val channelResponses = pendingResponses[channel]
        channelResponses?.get(user)?.remove(response)
    }

}