package me.vincentvibe3.emergencyfood.buttons.music.queue

import me.vincentvibe3.emergencyfood.utils.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object QueuePrev:InteractionButton() {

    override val name = "QueuePrev"

    override var button = Button.primary(name, "Prev")

    override fun handle(event: ButtonClickEvent) {
        super.handle(event)
    }

}