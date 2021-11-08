package me.vincentvibe3.emergencyfood.buttons.sauce.read

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.commands.sauce.Read
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button
import org.json.JSONException
import org.json.JSONObject

object SauceNext:InteractionButton() {
    override val name = "SauceNext"

    override val button = Button.primary(name, "Next")

    override suspend fun handle(event: ButtonClickEvent) {
        val originalEmbed = event.message.embeds.first()
        val description = originalEmbed.description
        val id = description?.substringAfter("[")?.substringBefore("]")
        val footer = originalEmbed.footer?.text
        if (footer != null) {
            val nextPage = footer.split(" ")[1].toInt() + 1
            lateinit var sauceInfo: JSONObject
            var infoOk = false
            try {
                sauceInfo = Read.getInfo(id)
                infoOk = true
            } catch (e: JSONException) {
                event.message.editMessage("An unknown error occurred").override(true).queue()
            } catch (e: RequestFailedException) {
                event.message.editMessage("An unknown error occurred").override(true).queue()
            }
            if (infoOk) {
                val pageCount = sauceInfo.getInt("num_pages")
                val image = Read.getImage(sauceInfo, nextPage)
                val embed = Templates.getSauceEmbed()
                    .setImage(image)
                    .setFooter("Page $nextPage of $pageCount")
                    .setDescription("[$id](https://nhentai.net/g/$id)")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .setActionRows(Read.getButtonsRow(nextPage, pageCount))
                    .build()
                event.message.editMessage(message).override(true).queue()
            }
        } else {
            event.message.editMessage("An unknown error occurred").override(true).queue()
        }
        event.reply("Changing Page").queue()
        event.hook.deleteOriginal().queue()
    }
}