package me.vincentvibe3.emergencyfood.buttons.sauce.read

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object SauceStart:InteractionButton() {
    override val name = "SauceStart"

    override val button = Button.primary(name, "First")

    override suspend fun handle(event: ButtonClickEvent) {
        TODO("Not yet implemented")
    }
}