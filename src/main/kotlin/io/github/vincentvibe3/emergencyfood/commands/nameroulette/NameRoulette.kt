package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.ModalManager
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

object NameRoulette:GenericCommand(), SlashCommand{

    init {
        ModalManager.registerLocal(EntryModal)
        registerSubCommands(Add)
        registerSubCommands(Roll)
        registerSubCommands(Setup)
        registerSubCommands(Registration)
        registerSubCommands(ListEntries)
    }

    override val name: String = "nameroulette"

    override val command: CommandData = Commands.slash(name, "nameroulette")
        .addSubcommands(Add.subCommand)
        .addSubcommands(Roll.subCommand)
        .addSubcommands(Setup.subCommand)
        .addSubcommands(Registration.subCommand)
        .addSubcommands(ListEntries.subCommand)

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        handleSubCommands(event)
    }
}