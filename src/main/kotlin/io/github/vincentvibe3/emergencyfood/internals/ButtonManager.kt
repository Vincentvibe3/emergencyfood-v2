package io.github.vincentvibe3.emergencyfood.internals

object ButtonManager {

    private val buttonsList = HashMap<String, InteractionButton>()

    //register buttons that can be listened for
    fun registerLocal(button: InteractionButton) {
        buttonsList[button.name] = button
    }

    //return all registered buttons
    fun getButtons(): HashMap<String, InteractionButton> {
        return buttonsList
    }
}