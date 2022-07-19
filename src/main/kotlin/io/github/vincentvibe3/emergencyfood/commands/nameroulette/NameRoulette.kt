package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.commands.anime.Programming
import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.ModalManager
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

object NameRoulette:GenericCommand(), SlashCommand{

    init {
        ModalManager.registerLocal(EntryModal)
        registerSubCommands(Add)
        registerSubCommands(Roll)
        registerSubCommands(Setup)
        registerSubCommands(Registration)
    }

    override val name: String = "nameroulette"

    override val command: CommandData = Commands.slash(name, "nameroulette")
        .addSubcommands(Add.subCommand)
        .addSubcommands(Roll.subCommand)
        .addSubcommands(Setup.subCommand)
        .addSubcommands(Registration.subCommand)

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        handleSubCommands(event)
    }
}