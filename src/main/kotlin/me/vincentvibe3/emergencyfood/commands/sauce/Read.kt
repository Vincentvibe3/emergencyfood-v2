package me.vincentvibe3.emergencyfood.commands.sauce

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueNext
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueuePrev
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueStart
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceEnd
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceNext
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SaucePrev
import me.vincentvibe3.emergencyfood.buttons.sauce.read.SauceStart
import me.vincentvibe3.emergencyfood.internals.ButtonManager
import me.vincentvibe3.emergencyfood.internals.GenericSubCommand
import me.vincentvibe3.emergencyfood.internals.SubCommand
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.json.JSONException
import org.json.JSONObject

object Read: GenericSubCommand(), SubCommand {
    override val name = "read"

    override val subCommand = SubcommandData("read", "Read a sauce")
        .addOption(OptionType.INTEGER,"numbers", "the number to the sauce", true)

    init {
        ButtonManager.registerLocal(SauceStart)
        ButtonManager.registerLocal(SauceNext)
        ButtonManager.registerLocal(SaucePrev)
        ButtonManager.registerLocal(SauceEnd)
    }

    suspend fun getInfo(id:String?): JSONObject {
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

    fun getImage(info:JSONObject, index:Int):String{
        val pageInfo = info.getJSONObject("images").getJSONArray("pages")
        val page = pageInfo.getJSONObject(index)
        val format = getImageFormat(page)
        val id = info.getString("media_id")
        return "https://i.nhentai.net/galleries/$id/${index+1}.$format"
    }

    fun getButtonsRow(currentPage:Int, lastPage:Int): ActionRow {
        return if (currentPage==lastPage&&lastPage!=1){
            ActionRow.of(
                SauceStart.getEnabled(),
                SaucePrev.getEnabled(),
                SauceNext.getDisabled(),
                SauceEnd.getDisabled()
            )
        } else if (currentPage==1){
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

    override suspend fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val id = event.getOption("numbers")?.asString
        if (id != null){
            lateinit var sauceInfo:JSONObject
            var infoOk = false
            try {
                sauceInfo = getInfo(id)
                infoOk = true
            } catch (e:JSONException){
                event.hook.editOriginal("An unknown error occurred").queue()
            } catch (e:RequestFailedException){
                event.hook.editOriginal("An unknown error occurred").queue()
            }
            if (infoOk){
                val pageCount = sauceInfo.getInt("num_pages")
                val image = getImage(sauceInfo, 0)
                val embed = Templates.getSauceEmbed()
                    .setImage(image)
                    .setFooter("Page 1 of $pageCount")
                    .setDescription("[$id](https://nhentai.net/g/$id)")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .setActionRows(getButtonsRow(1, pageCount))
                    .build()
                event.hook.editOriginal(message).queue()
            }

        }
    }
}