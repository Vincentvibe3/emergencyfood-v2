package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import java.util.UUID

abstract class InteractionSelectMenu:InteractionManager {

    companion object {
        const val DEFAULT_EXPIRY_OFFSET:Long = 900000
    }

    //SelectMenu name
    abstract val name: String

    //uuid for componentId
    open val uuid: UUID = UUID.randomUUID()

    //menu to be created
    abstract val menu: StringSelectMenu

    open val expires = false

    open var expiry:Long? = null

    //generic function used as the entry point to handle a SelectMenuInteractionEvent
    abstract suspend fun handle(event: StringSelectInteraction)

}