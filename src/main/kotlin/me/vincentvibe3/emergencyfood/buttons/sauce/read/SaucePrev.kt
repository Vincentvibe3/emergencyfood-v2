package me.vincentvibe3.emergencyfood.buttons.sauce.read

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object SaucePrev:InteractionButton() {
    override val name = "SaucePrev"

    override val button = Button.primary(name, "Prev")

    override suspend fun handle(event: ButtonClickEvent) {
        TODO("Not yet implemented")
    }
}