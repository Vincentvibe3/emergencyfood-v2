package me.vincentvibe3.emergencyfood.buttons.sauce.read

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object SauceEnd:InteractionButton() {
    override val name = "SauceEnd"

    override val button = Button.secondary(name, "Last")

    override suspend fun handle(event: ButtonClickEvent) {
        TODO("Not yet implemented")
    }
}