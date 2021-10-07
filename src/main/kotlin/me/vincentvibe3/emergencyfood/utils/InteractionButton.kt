package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

abstract class InteractionButton {

    abstract val name:String

    abstract val button:Button

    fun getDisabled():Button {
        return button.asDisabled()
    }

    fun getEnabled():Button {
        return button.asEnabled()
    }

    open fun handle(event: ButtonClickEvent) {}
}