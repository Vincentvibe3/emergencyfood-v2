package me.vincentvibe3.emergencyfood.commands.sauce

import com.github.kittinunf.fuel.httpGet
import me.vincentvibe3.emergencyfood.internals.SubCommand
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.json.JSONObject

object Random: SubCommand {
    override val name = "random"

    override val subCommand = SubcommandData(name, "Get a random sauce")

    fun search(query:String):JSONObject{
        lateinit var jsonResponse:JSONObject
        var requestSuccess = true
        val httpAsync = "https://nhentai.net/api/galleries/search?query=$query"
            .httpGet()
            .responseString { request, response, result ->
                val (responseText, _) = result
                if (responseText != null && response.statusCode == 200){
                    jsonResponse = JSONObject(responseText)
                } else {
                    requestSuccess = false
                }
            }
        httpAsync.join()
        if (!requestSuccess){
            throw RequestFailedException()
        }
        return jsonResponse
    }

    override suspend fun handle(event: SlashCommandEvent) {
        if (event.textChannel.isNSFW){

        } else {
            event.reply("You must use a NSFW channel for this").queue()
        }
    }
}