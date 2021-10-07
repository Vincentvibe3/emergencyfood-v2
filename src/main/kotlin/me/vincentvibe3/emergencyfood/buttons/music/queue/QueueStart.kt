package me.vincentvibe3.emergencyfood.buttons.music.queue

import me.vincentvibe3.emergencyfood.utils.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object QueueStart:InteractionButton() {

    override val name = "QueueStart"

    override var button = Button.secondary(name, "First")

    override fun handle(event: ButtonClickEvent) {
        super.handle(event)
    }

}