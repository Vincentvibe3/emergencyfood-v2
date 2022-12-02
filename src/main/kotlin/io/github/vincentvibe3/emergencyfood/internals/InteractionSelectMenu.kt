package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

abstract class InteractionSelectMenu {

    //SelectMenu name or ComponentId
    abstract val name: String

    //menu to be created
    abstract val menu: StringSelectMenu.Builder

    open val expires = false

    open var expiry:Long? = null

    //generic function used as the entry point to handle a SelectMenuInteractionEvent
    abstract suspend fun handle(event: StringSelectInteraction)

}