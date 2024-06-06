package io.github.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

object ModalManager {

    private val modalList = HashMap<UUID, InteractionModal>()

    //register modals that can be listened for
    fun registerLocal(modal: InteractionModal) {
        modalList[modal.uuid] = modal
        if (modal.expires) {
            InteractionManager.coroutineScope.launch {
                modal.expiry?.let { delay(it) }
                removeLocal(modal)
            }
        }
    }

    private fun removeLocal(modal: InteractionModal){
        modalList.remove(modal.uuid)
    }

    //return all registered modals
    fun getModals(): HashMap<UUID, InteractionModal> {
        return modalList
    }
}