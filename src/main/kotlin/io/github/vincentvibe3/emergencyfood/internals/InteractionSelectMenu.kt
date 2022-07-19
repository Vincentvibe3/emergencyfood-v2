package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

abstract class InteractionSelectMenu {

    //SelectMenu name or ComponentId
    abstract val name: String

    //menu to be created
    abstract val menu: SelectMenu.Builder

    open val expires = false

    open var expiry:Long? = null

    //generic function used as the entry point to handle a SelectMenuInteractionEvent
    abstract suspend fun handle(event: SelectMenuInteractionEvent)

}