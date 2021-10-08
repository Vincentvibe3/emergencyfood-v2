package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

abstract class InteractionButton {

    //button name or ComponentId
    abstract val name:String

    //Button to be created
    abstract val button:Button

    //returns the button as disabled
    fun getDisabled():Button {
        return button.asDisabled()
    }

    //returns the button as enabled
    fun getEnabled():Button {
        return button.asEnabled()
    }

    //generic function used as the entry point to handle a ButtonClickEvent
    open fun handle(event: ButtonClickEvent) {}
}