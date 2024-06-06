package io.github.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ButtonManager:InteractionManager {

    private val buttonsList = HashMap<String, InteractionButton>()

    //register buttons that can be listened for
    fun registerLocal(button: InteractionButton) {
        buttonsList[button.name] = button
        if (button.expires) {
            InteractionManager.coroutineScope.launch {
                button.expiry?.let { delay(it) }
                unregisterLocal(button)
            }
        }
    }

    private fun unregisterLocal(button:InteractionButton){
        buttonsList.remove(button.name)
    }

    //return all registered buttons
    fun getButtons(): HashMap<String, InteractionButton> {
        return buttonsList
    }
}