package io.github.vincentvibe3.emergencyfood.commands.anime

import io.ktor.http.*
import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.json.JSONArray
import org.json.JSONException
import kotlin.random.Random
import kotlin.random.nextInt

object Programming:GenericSubCommand(), SubCommand, MessageSubCommand {

    override val name = "programming"

    override val subCommand = SubcommandData(name, "get an anime girl with a programming book")

    private suspend fun getCategory():String{
        lateinit var category: String
        lateinit var categories: String
        try {
            categories = RequestHandler.get("https://api.github.com/repos/laynH/Anime-Girls-Holding-Programming-Books/contents/")
        } catch (e:RequestFailedException){
            throw e
        }
        val jsonCategories = JSONArray(categories)
        val max = jsonCategories.length()-1
        var ok = false
        while (!ok){
            lateinit var type:String
            val choice = Random.nextInt(0..max)
            val json = jsonCategories.getJSONObject(choice)
            try {
                type = json.getString("type")
                category = json.getString("name")
            } catch (e:JSONException){ }
            ok = type=="dir"
        }
        return "https://api.github.com/repos/laynH/Anime-Girls-Holding-Programming-Books/contents/$category"
    }

    private suspend fun getImage(catUrl:String):String{
        lateinit var category:String
        try {
            category = RequestHandler.get(catUrl)
        } catch (e:RequestFailedException){
            throw e
        }
        val jsonCategory = JSONArray(category)
        val max = jsonCategory.length()-1
        val choice = Random.nextInt(0..max)
        val json = jsonCategory.getJSONObject(choice)
        return json.getString("download_url")
    }

    override suspend fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val categoryUrl = getCategory()
        val image = getImage(categoryUrl)
        event.hook.editOriginal(image).queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        println("received")
        val categoryUrl = getCategory()
        val image = getImage(categoryUrl)
        event.textChannel.sendMessage(image).queue()
    }
}