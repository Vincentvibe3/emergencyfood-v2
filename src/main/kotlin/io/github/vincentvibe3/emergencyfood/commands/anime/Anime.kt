package io.github.vincentvibe3.emergencyfood.commands.anime

import io.github.vincentvibe3.emergencyfood.internals.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Anime:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "anime"

    override val command = CommandData(name, "anime related commands")
        .addSubcommands(
            Programming.subCommand
        )

    init {
        registerSubCommands(Programming)
    }

    override suspend fun handle(event: SlashCommandEvent) {
        handleSubCommands(event)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        handleMessageSubCommands(event)
    }

}