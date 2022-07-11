package io.github.vincentvibe3.emergencyfood.commands.anime

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Anime : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "anime"

    override val command = Commands.slash(name, "anime related commands")
        .addSubcommands(
            Programming.subCommand
        )

    init {
        registerSubCommands(Programming)
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        handleSubCommands(event)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        handleMessageSubCommands(event)
    }

}