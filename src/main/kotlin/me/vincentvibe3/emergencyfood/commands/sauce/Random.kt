package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.internals.GenericCommand
import me.vincentvibe3.emergencyfood.internals.GenericSubCommand
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.internals.SubCommand
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.json.JSONException
import org.json.JSONObject

object Random: GenericSubCommand(), SubCommand{
    override val name = "random"

    override val subCommand = SubcommandData(name, "Get a random sauce")

    suspend fun search(query:String):JSONObject{
        lateinit var jsonResponse:JSONObject

        try{
            val response = RequestHandler.get("https://nhentai.net/api/galleries/search?query=$query")
            jsonResponse = JSONObject(response)
        } catch (e:RequestFailedException){
            throw e
        } catch (e:JSONException){
            throw e
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