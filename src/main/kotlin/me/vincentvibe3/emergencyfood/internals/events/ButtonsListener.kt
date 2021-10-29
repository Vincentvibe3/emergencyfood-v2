package me.vincentvibe3.emergencyfood.internals.events

import me.vincentvibe3.emergencyfood.internals.ButtonManager
import me.vincentvibe3.emergencyfood.internals.InteractionButton
import me.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ButtonsListener: ListenerAdapter()  {

    //respond to a button being clicked
    override fun onButtonClick(event: ButtonClickEvent) {
        Logging.logger.debug("Button ${event.componentId} pressed")
        retrieveButton(event.componentId)?.handle(event)
    }

    //get a button
    private fun retrieveButton(name:String): InteractionButton?{
        return ButtonManager.getButtons()[name]
    }
}