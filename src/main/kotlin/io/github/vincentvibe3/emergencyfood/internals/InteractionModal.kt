package io.github.vincentvibe3.emergencyfood.internals

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.modals.Modal
import java.util.*

abstract class InteractionModal {

    companion object {
        const val DEFAULT_EXPIRY_OFFSET:Long = 900000
    }

    //modal name or ComponentId
    abstract val name: String

    //Modal to be created
    abstract val modal: Modal?

    open val uuid: UUID = UUID.randomUUID()

    open val expires = false

    open var expiry:Long? = null

    //generic function used as the entry point to handle a ModalInteractionEvent
    abstract suspend fun handle(event: ModalInteractionEvent)

}