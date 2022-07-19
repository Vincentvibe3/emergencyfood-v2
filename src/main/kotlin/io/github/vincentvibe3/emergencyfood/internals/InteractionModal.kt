package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal

abstract class InteractionModal {

    //modal name or ComponentId
    abstract val name: String

    //Modal to be created
    abstract val modal: Modal

    //generic function used as the entry point to handle a ModalInteractionEvent
    abstract suspend fun handle(event: ModalInteractionEvent)

}