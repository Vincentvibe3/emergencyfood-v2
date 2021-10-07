package me.vincentvibe3.emergencyfood.buttons.music.queue

import me.vincentvibe3.emergencyfood.utils.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object QueueEnd:InteractionButton() {

    override val name = "QueueEnd"

    override var button = Button.secondary(name, "Last")

    override fun handle(event: ButtonClickEvent) {
        super.handle(event)
    }

}