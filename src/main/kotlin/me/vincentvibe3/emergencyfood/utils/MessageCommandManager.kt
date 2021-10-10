package me.vincentvibe3.emergencyfood.utils

import me.vincentvibe3.emergencyfood.commands.admin.Admin

object MessageCommandManager {

    private val commandsList = HashMap<String, MessageCommand>()

    init {
        registerLocal(Admin)
    }

    private fun registerLocal(command: MessageCommand){
        commandsList[command.name] = command
    }

    fun getCommands():HashMap<String, MessageCommand> {
        return commandsList
    }

}