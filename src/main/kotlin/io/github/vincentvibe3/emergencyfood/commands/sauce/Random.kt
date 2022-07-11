package io.github.vincentvibe3.emergencyfood.commands.sauce

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random
import kotlin.random.nextInt

object Random : GenericSubCommand(), SubCommand, MessageSubCommand {
    override val name = "random"

    override val subCommand = SubcommandData(name, "Get a random sauce")
        .addOption(OptionType.STRING, "query", "Search query for a random sauce", false)
        .addOption(
            OptionType.BOOLEAN,
            "strict",
            "uses query as words to match in tags returning only if all are found",
            false
        )

    private suspend fun search(query: String): JSONObject {
        lateinit var jsonResponse: JSONObject
        try {
            val response = RequestHandler.get("https://nhentai.net/api/galleries/search?query=$query")
            jsonResponse = JSONObject(response)
        } catch (e: RequestFailedException) {
            throw e
        } catch (e: JSONException) {
            throw e
        }
        return jsonResponse
    }

    private suspend fun getPage(response: JSONObject, query: String): JSONObject? {
        val totalPages = response.getInt("num_pages")
        if (totalPages < 1) {
            return null
        }
        val pageToGet = Random.nextInt(1..totalPages)
        lateinit var jsonResponse: JSONObject
        try {
            val pageResponse =
                RequestHandler.get("https://nhentai.net/api/galleries/search?query=$query&page=$pageToGet")
            jsonResponse = JSONObject(pageResponse)
        } catch (e: RequestFailedException) {
            throw e
        } catch (e: JSONException) {
            throw e
        }
        return jsonResponse
    }

    private fun getEntry(response: JSONObject, strict: Boolean, query: String): String {
        var found = false
        val searchTags = query.split("+")
        lateinit var entry: JSONObject
        val entries = response.getJSONArray("result")
        val max = entries.length() - 1
        var attempts = 0
        while (!found && attempts <= max) {
            val entryIndex = Random.nextInt(0..max)
            entry = entries.getJSONObject(entryIndex)
            val tags = entry.getJSONArray("tags")
            val tagNames = ArrayList<String>()
            tags.forEach {
                if (it is JSONObject) {
                    tagNames.add(it.getString("name"))
                }
            }
            found = if (strict) {
                searchTags.size == searchTags
                    .filter { tagName -> tagNames.any { it == tagName } }
                    .size
            } else {
                true
            }
            attempts++
        }
        return if (found) {
            val id = entry.getInt("id")
            "https://nhentai.net/g/$id"
        } else {
            "Could not find a matching sauce. Try Again"
        }

    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val textChannel = event.textChannel
        val options = event.getOptions()
        var tags: String? = ""
        var strict = false
        if (options.isEmpty()) {
            tags = null
        } else if (options.last().lowercase().toBooleanStrictOrNull() == null || options.size == 1) {
            options.subList(0, options.size).forEach { tags += "$it " }
        } else {
            options.subList(0, options.size - 1).forEach { tags += "$it " }
            strict = options.last().lowercase().toBooleanStrict()
        }
        var query = tags?.trim()?.replace(" ", "+")
        if (query == null) {
            query = "english"
        }
        try {
            val pages = search(query)
            val page = getPage(pages, query)
            if (page != null) {
                val url = getEntry(page, strict, query)
                textChannel.sendMessage(url).queue()
            } else {
                textChannel.sendMessage("No result was found").queue()
            }
        } catch (e: JSONException) {
            textChannel.sendMessage("An unknown error occurred").queue()
        } catch (e: RequestFailedException) {
            textChannel.sendMessage("An unknown error occurred").queue()
        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        var query = event.getOption("query")?.asString?.replace(" ", "+")
        var strict = event.getOption("strict")?.asBoolean
        if (query == null) {
            query = "english"
        }
        if (strict == null) {
            strict = false
        }
        try {
            val pages = search(query)
            val page = getPage(pages, query)
            if (page != null) {
                val url = getEntry(page, strict, query)
                event.hook.editOriginal(url).queue()
            } else {
                event.hook.editOriginal("No result was found").queue()
            }
        } catch (e: JSONException) {
            event.hook.editOriginal("An unknown error occurred").queue()
        } catch (e: RequestFailedException) {
            event.hook.editOriginal("An unknown error occurred").queue()
        }
    }
}