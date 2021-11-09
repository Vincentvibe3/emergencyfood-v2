package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceEnd
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceNext
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SaucePrev
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceStart
import me.vincentvibe3.emergencyfood.internals.ButtonManager
import me.vincentvibe3.emergencyfood.internals.GenericSubCommand
import me.vincentvibe3.emergencyfood.internals.MessageSubCommand
import me.vincentvibe3.emergencyfood.internals.SubCommand
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.json.JSONException
import org.json.JSONObject

object Read: GenericSubCommand(), SubCommand, MessageSubCommand {
    override val name = "read"

    override val subCommand = SubcommandData("read", "Read a sauce")
        .addOption(OptionType.INTEGER,"numbers", "the number to the sauce", true)
        .addOption(OptionType.INTEGER, "start", "the page to start on", false)

    init {
        ButtonManager.registerLocal(SauceStart)
        ButtonManager.registerLocal(SauceNext)
        ButtonManager.registerLocal(SaucePrev)
        ButtonManager.registerLocal(SauceEnd)
    }

    private suspend fun getInfo(id:String?): JSONObject {
        lateinit var jsonResponse: JSONObject
        try{
            val response = RequestHandler.get("https://nhentai.net/api/gallery/$id")
            jsonResponse = JSONObject(response)
        } catch (e: RequestFailedException){
            throw e
        } catch (e: JSONException){
            throw e
        }
        return jsonResponse
    }

    private fun getImageFormat(image:JSONObject):String{
        lateinit var ext:String
        when(image.getString("t")){
            "j" -> ext = "jpg"
            "p" -> ext = "png"
            "g" -> ext = "gif"
        }
        return ext
    }

    private fun getImage(info:JSONObject, index: Long):String{
        //index starts at 1
        val pageInfo = info.getJSONObject("images").getJSONArray("pages")
        val page = pageInfo.getJSONObject((index-1).toInt())
        val format = getImageFormat(page)
        val id = info.getString("media_id")
        return "https://i.nhentai.net/galleries/$id/${index}.$format"
    }

    private fun getButtonsRow(currentPage:Long, lastPage:Long): ActionRow {
        return if (currentPage==lastPage&&lastPage!=1L){
            ActionRow.of(
                SauceStart.getEnabled(),
                SaucePrev.getEnabled(),
                SauceNext.getDisabled(),
                SauceEnd.getDisabled()
            )
        } else if (currentPage==1L){
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

    suspend fun getMessage(id:String, currentPage: Long):Message {
        //currentPage is the index of the page
        //Note: starts at 1
        lateinit var sauceInfo: JSONObject
        try {
            sauceInfo = getInfo(id)
        } catch (e: JSONException) {
            return MessageBuilder().setContent("An unknown error occurred").build()
        } catch (e: RequestFailedException) {
            return MessageBuilder().setContent("An unknown error occurred").build()
        }
        val pageCount = sauceInfo.getLong("num_pages")
        val image = getImage(sauceInfo, currentPage)
        val embed = Templates.getSauceEmbed()
            .setImage(image)
            .setFooter("Page $currentPage of $pageCount")
            .setDescription("Now Reading: [$id](https://nhentai.net/g/$id)")
            .build()
        return MessageBuilder()
            .setEmbeds(embed)
            .setActionRows(getButtonsRow(currentPage, pageCount))
            .build()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val textChannel = event.textChannel
        val options = event.getOptions()
        val id = options.getOrNull(0)
        val page = if (options.size>=2){
            options[2].toLongOrNull()
        } else {
            1L
        }
        if (id != null&&page!=null){
            val message = getMessage(id, page)
            textChannel.sendMessage(message).queue()
        } else if (id == null){
            textChannel.sendMessage("A sauce is required").queue()
        } else if (page == null) {
            textChannel.sendMessage("The page number must be a number").queue()
        } else {
            textChannel.sendMessage("An unknown error has occurred").queue()
        }
    }

    override suspend fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val id = event.getOption("numbers")?.asString
        var page = event.getOption("start")?.asLong
        if (id != null){
            if (page==null){
                page = 1L
            }
            val message = getMessage(id, page)
            event.hook.editOriginal(message).queue()
        }
    }
}
