package io.github.vincentvibe3.emergencyfood.buttons.sauce.read

import io.github.vincentvibe3.emergencyfood.commands.sauce.Read
import io.github.vincentvibe3.emergencyfood.internals.InteractionButton
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

object SauceStart : InteractionButton() {
    override val name = "SauceStart"

    override val button = Button.primary(name, "First")

    override suspend fun handle(event: ButtonInteractionEvent) {
        val originalEmbed = event.message.embeds.firstOrNull()
        val description = originalEmbed?.description
        val id = description?.substringAfter("[")?.substringBefore("]")
        val footer = originalEmbed?.footer?.text
        if (footer != null) {
            val nextPage = 1L
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