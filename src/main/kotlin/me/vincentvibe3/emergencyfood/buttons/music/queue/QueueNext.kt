package me.vincentvibe3.emergencyfood.buttons.music.queue

import me.vincentvibe3.emergencyfood.utils.InteractionButton
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

object QueueNext: InteractionButton() {
    override val name = "QueueNext"

    override val button = Button.primary(name, "Next")

    override fun handle(event: ButtonClickEvent) {
        val buttons = ActionRow.of(
            QueueStart.getDisabled(),
            QueuePrev.getDisabled(),
            QueueNext.getEnabled(),
            QueueEnd.getDisabled()
        )
        val message = MessageBuilder()
            .setContent("Updated")
            .setActionRows(buttons)
            .build()
        event.message.editMessage(message).override(true).queue()
        event.reply("Updated queue").queue()
        event.hook.deleteOriginal().queue()
    }
}