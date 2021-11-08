package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.internals.GenericSubCommand
import me.vincentvibe3.emergencyfood.internals.SubCommand
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random
import kotlin.random.nextInt

object Random: GenericSubCommand(), SubCommand{
    override val name = "random"

    override val subCommand = SubcommandData(name, "Get a random sauce")
        .addOption(OptionType.STRING, "query", "Search query for a random sauce", false)

    private suspend fun search(query:String):JSONObject{
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

    private suspend fun getPage(response:JSONObject, query: String):JSONObject?{
        val totalPages = response.getInt("num_pages")
        if (totalPages<1){
            return null
        }
        val pageToGet = Random.nextInt(1..totalPages)
        lateinit var jsonResponse:JSONObject
        try{
            val response = RequestHandler.get("https://nhentai.net/api/galleries/search?query=$query&page=$pageToGet")
            jsonResponse = JSONObject(response)
        } catch (e:RequestFailedException){
            throw e
        } catch (e:JSONException){
            throw e
        }
        return jsonResponse
    }

    private suspend fun getEntry(response: JSONObject):String{
        val entries = response.getJSONArray("result")
        val max = entries.length()-1
        val entryIndex = Random.nextInt(0..max)
        val entry = entries.getJSONObject(entryIndex)
        val id = entry.getInt("id")
        return "https://nhentai.net/g/$id"
    }

    override suspend fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        var query = event.getOption("query")?.asString?.replace(" ", "+")
        if (query==null){
            query = "english"
        }
        try{
            val pages = search(query)
            val page = getPage(pages, query)
            if (page!=null){
                val url = getEntry(page)
                event.hook.editOriginal(url).queue()
            } else {
                event.hook.editOriginal("No result was found").queue()
            }
        } catch (e:JSONException){
            event.hook.editOriginal("An unknown error occurred").queue()
        } catch (e:RequestFailedException){
            event.hook.editOriginal("An unknown error occurred").queue()
        }
    }
}