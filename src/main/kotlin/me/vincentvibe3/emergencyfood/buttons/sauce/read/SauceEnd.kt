package me.vincentvibe3.emergencyfood.buttons.sauce.read

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.commands.sauce.Read
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object SauceEnd:InteractionButton() {
    override val name = "SauceEnd"

    override val button = Button.secondary(name, "Last")

    override suspend fun handle(event: ButtonClickEvent) {
        val originalEmbed = event.message.embeds.firstOrNull()
        val description = originalEmbed?.description
        val id = description?.substringAfter("[")?.substringBefore("]")
        val footer = originalEmbed?.footer?.text
        if (footer != null) {
            val nextPage = footer.split(" ")[3].toLong()
            if (id != null) {
                val message = Read.getMessage(id, nextPage)
                event.message.editMessage(message).override(true).queue()
            }
        } else {
            event.message.editMessage("An unknown error occurred").override(true).queue()
        }
        event.reply("Changing Page").queue()
        event.hook.deleteOriginal().queue()
    }
}