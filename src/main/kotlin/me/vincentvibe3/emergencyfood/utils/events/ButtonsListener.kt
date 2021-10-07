package me.vincentvibe3.emergencyfood.utils.events

import me.vincentvibe3.emergencyfood.utils.ButtonManager
import me.vincentvibe3.emergencyfood.utils.InteractionButton
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ButtonsListener: ListenerAdapter()  {

    override fun onButtonClick(event: ButtonClickEvent) {
        println(event.componentId)
        retrieveButton(event.componentId)?.handle(event)
    }

    private fun retrieveButton(name:String): InteractionButton?{
        return ButtonManager.getButtons()[name]
    }
}