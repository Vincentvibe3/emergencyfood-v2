package me.vincentvibe3.emergencyfood.internals

abstract class GenericCommand {

    //name of the command
    abstract val name: String

    val subCommands = HashMap<String, GenericSubCommand>()

    fun registerSubCommands(subCommand: GenericSubCommand) {
        this.subCommands[subCommand.name] = subCommand
    }

}