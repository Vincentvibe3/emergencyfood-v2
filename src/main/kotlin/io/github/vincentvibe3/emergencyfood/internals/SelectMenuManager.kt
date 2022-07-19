package io.github.vincentvibe3.emergencyfood.internals

object SelectMenuManager {

    private val menusList = HashMap<String, InteractionSelectMenu>()

    //register buttons that can be listened for
    fun registerLocal(menu: InteractionSelectMenu) {
        menusList[menu.name] = menu
    }

    fun unregisterLocal(menu:InteractionSelectMenu){
        menusList.remove(menu.name)
    }

    //return all registered buttons
    fun getMenus(): HashMap<String, InteractionSelectMenu> {
        return menusList
    }

}