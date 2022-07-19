package io.github.vincentvibe3.emergencyfood.internals.events

import io.github.vincentvibe3.emergencyfood.internals.ButtonManager
import io.github.vincentvibe3.emergencyfood.internals.InteractionButton
import io.github.vincentvibe3.emergencyfood.internals.InteractionModal
import io.github.vincentvibe3.emergencyfood.internals.ModalManager
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ModalListener : ListenerAdapter() {

    //respond to a button being clicked
    override fun onModalInteraction(event: ModalInteractionEvent) {
        Logging.logger.debug("Modal ${event.modalId} triggered")
        GlobalScope.launch {
            retrieveModal(event.modalId)?.handle(event)
        }
    }

    //get a button
    private fun retrieveModal(name: String): InteractionModal? {
        return ModalManager.getModals()[name]
    }
}