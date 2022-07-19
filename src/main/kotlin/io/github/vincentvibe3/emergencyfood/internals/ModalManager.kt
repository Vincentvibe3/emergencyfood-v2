package io.github.vincentvibe3.emergencyfood.internals

object ModalManager {

    private val modalList = HashMap<String, InteractionModal>()

    //register modals that can be listened for
    fun registerLocal(modal: InteractionModal) {
        modalList[modal.name] = modal
    }

    //return all registered modals
    fun getModals(): HashMap<String, InteractionModal> {
        return modalList
    }
}