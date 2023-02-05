package io.github.vincentvibe3.emergencyfood.commands.anime

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.serialization.GithubApiCategory
import io.github.vincentvibe3.emergencyfood.serialization.GithubApiImages
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import kotlin.random.Random
import kotlin.random.nextInt

object Programming : GenericSubCommand(), SubCommand, MessageSubCommand {

    override val name = "programming"

    override val subCommand = SubcommandData(name, "get an anime girl with a programming book")

    private val deserializer = Json { ignoreUnknownKeys = true }

    private suspend fun getCategory(): String {
        lateinit var category: String
        lateinit var categories: String
        try {
            categories = RequestHandler.get("https://api.github.com/repos/cat-milk/Anime-Girls-Holding-Programming-Books/contents/")
        } catch (e:RequestFailedException){
            throw e
        }
        val jsonCategories = deserializer.decodeFromString<List<GithubApiCategory>>(categories)
        val max = jsonCategories.size - 1
        var ok = false
        while (!ok) {
            lateinit var type: String
            val choice = Random.nextInt(0..max)
            val json = jsonCategories[choice]
            type = json.type
            category = json.name
            ok = type == "dir"
        }
        return "https://api.github.com/repos/cat-milk/Anime-Girls-Holding-Programming-Books/contents/$category"
    }

    private suspend fun getImage(catUrl: String): String {
        lateinit var category: String
        try {
            category = RequestHandler.get(catUrl)
        } catch (e: RequestFailedException) {
            throw e
        }
        val jsonCategory = deserializer.decodeFromString<List<GithubApiImages>>(category)
        val max = jsonCategory.size - 1
        val choice = Random.nextInt(0..max)
        val json = jsonCategory[choice]
        return json.download_url
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val categoryUrl = getCategory()
        val image = getImage(categoryUrl)
        event.hook.editOriginal(image).queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val categoryUrl = getCategory()
        val image = getImage(categoryUrl)
        event.guildChannel.sendMessage(image).queue()
    }
}