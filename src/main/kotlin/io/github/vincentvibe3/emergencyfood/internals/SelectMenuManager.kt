package io.github.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

object SelectMenuManager:InteractionManager {

    private val menusList = HashMap<UUID, InteractionSelectMenu>()

    //register buttons that can be listened for
    fun registerLocal(menu: InteractionSelectMenu) {
        menusList[menu.uuid] = menu
        if (menu.expires) {
            InteractionManager.coroutineScope.launch {
                menu.expiry?.let { delay(it) }
                unregisterLocal(menu)
            }
        }
    }

    private fun unregisterLocal(menu:InteractionSelectMenu){
        menusList.remove(menu.uuid)
    }

    //return all registered buttons
    fun getMenus(): HashMap<UUID, InteractionSelectMenu> {
        return menusList
    }

}