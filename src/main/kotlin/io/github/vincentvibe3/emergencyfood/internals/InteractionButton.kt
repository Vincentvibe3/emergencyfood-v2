package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

abstract class InteractionButton {

    //button name or ComponentId
    abstract val name: String

    //Button to be created
    abstract val button: Button

    open val expires = false

    open var expiry:Long? = null

    //returns the button as disabled
    fun getDisabled(): Button {
        return button.asDisabled()
    }

    //returns the button as enabled
    fun getEnabled(): Button {
        return button.asEnabled()
    }

    //generic function used as the entry point to handle a ButtonClickEvent
    abstract suspend fun handle(event: ButtonInteractionEvent)

}