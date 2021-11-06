package me.vincentvibe3.emergencyfood.commands.anime

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.internals.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData


@Bot.Beta
object Anime:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "anime"

    override val command = CommandData(name, "anime relatd commands")
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