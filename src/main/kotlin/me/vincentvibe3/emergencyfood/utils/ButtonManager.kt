package me.vincentvibe3.emergencyfood.utils

object ButtonManager {

    private val buttonsList = HashMap<String, InteractionButton>()

    fun registerLocal(button: InteractionButton){
        buttonsList[button.name] = button
    }

    fun getButtons():HashMap<String, InteractionButton> {
        return buttonsList
    }
}