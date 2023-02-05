package io.github.vincentvibe3.emergencyfood.commands.sauce

import io.github.vincentvibe3.emergencyfood.buttons.sauce.read.SauceEnd
import io.github.vincentvibe3.emergencyfood.buttons.sauce.read.SauceNext
import io.github.vincentvibe3.emergencyfood.buttons.sauce.read.SaucePrev
import io.github.vincentvibe3.emergencyfood.buttons.sauce.read.SauceStart
import io.github.vincentvibe3.emergencyfood.internals.ButtonManager
import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData

object Read : GenericSubCommand(), SubCommand, MessageSubCommand {
    override val name = "read"

    override val subCommand = SubcommandData("read", "Read a sauce")
        .addOption(OptionType.INTEGER, "numbers", "the number to the sauce", true)
        .addOption(OptionType.INTEGER, "start", "the page to start on", false)

    init {
        ButtonManager.registerLocal(SauceStart)
        ButtonManager.registerLocal(SauceNext)
        ButtonManager.registerLocal(SaucePrev)
        ButtonManager.registerLocal(SauceEnd)
    }

    private suspend fun getInfo(id: String?): JsonObject {
        return try {
            val response = RequestHandler.get("https://nhentai.net/api/gallery/$id")
            Json.decodeFromString(response)
        } catch (e: RequestFailedException) {
            throw e
        }
    }

    private fun getImageFormat(image: JsonObject): String {
        lateinit var ext: String
        val type = if (image["t"] is JsonPrimitive){
            image["t"]?.jsonPrimitive?.content
        } else {
            return ""
        }
        when (type) {
            "j" -> ext = "jpg"
            "p" -> ext = "png"
            "g" -> ext = "gif"
        }
        return ext
    }

    private fun getImage(info: JsonObject, index: Long): String {
        //index starts at 1
        val pageInfo = info["images"]?.jsonObject?.get("pages")?.jsonArray
        val page = pageInfo?.get((index - 1).toInt())?.jsonObject
        val format = page?.let { getImageFormat(it) }
        val id = info["media_id"]?.jsonPrimitive?.content
        return "https://i.nhentai.net/galleries/$id/${index}.$format"
    }

    private fun getButtonsRow(currentPage: Long, lastPage: Long): ActionRow {
        return if (currentPage == lastPage && lastPage != 1L) {
            ActionRow.of(
                SauceStart.getEnabled(),
                SaucePrev.getEnabled(),
                SauceNext.getDisabled(),
                SauceEnd.getDisabled()
            )
        } else if (currentPage == 1L) {
            ActionRow.of(
                SauceStart.getDisabled(),
                SaucePrev.getDisabled(),
                SauceNext.getEnabled(),
                SauceEnd.getEnabled()
            )
        } else {
            ActionRow.of(
                SauceStart.getEnabled(),
                SaucePrev.getEnabled(),
                SauceNext.getEnabled(),
                SauceEnd.getEnabled()
            )
        }

    }

    suspend fun getMessage(id: String, currentPage: Long): MessageCreateData {
        //currentPage is the index of the page
        //Note: starts at 1
        val sauceInfo = try {
            getInfo(id)
        } catch (e: RequestFailedException) {
            return MessageCreateBuilder().setContent("An unknown error occurred").build()
        }
        val pageCount = sauceInfo["num_pages"]?.jsonPrimitive?.long
        val image = getImage(sauceInfo, currentPage)
        val embed = Templates.getSauceEmbed()
            .setImage(image)
            .setFooter("Page $currentPage of $pageCount")
            .setDescription("Now Reading: [$id](https://nhentai.net/g/$id)")
            .build()
        return MessageCreateBuilder()
            .setEmbeds(embed)
            .setComponents(pageCount?.let { getButtonsRow(currentPage, it) })
            .build()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val textChannel = event.guildChannel
        val options = event.getOptions()
        val id = options.getOrNull(0)
        val page = if (options.size >= 2) {
            options[2].toLongOrNull()
        } else {
            1L
        }
        if (id != null && page != null) {
            val message = getMessage(id, page)
            textChannel.sendMessage(message).queue()
        } else if (id == null) {
            textChannel.sendMessage("A sauce is required").queue()
        } else if (page == null) {
            textChannel.sendMessage("The page number must be a number").queue()
        } else {
            textChannel.sendMessage("An unknown error has occurred").queue()
        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val id = event.getOption("numbers")?.asString
        var page = event.getOption("start")?.asLong
        if (id != null) {
            if (page == null) {
                page = 1L
            }
            val message = getMessage(id, page)
            event.hook.editOriginal(MessageEditData.fromCreateData(message)).queue()
        }
    }
}
