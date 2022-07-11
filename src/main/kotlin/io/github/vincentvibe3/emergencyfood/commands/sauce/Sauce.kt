package io.github.vincentvibe3.emergencyfood.commands.sauce

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Sauce : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "sauce"

    override val command = Commands.slash(name, "Play a song or resume playback")
        .addSubcommands(
            Random.subCommand,
            Read.subCommand
        )

    init {
        registerSubCommands(Read)
        registerSubCommands(Random)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        if (event.textChannel.isNSFW) {
            handleMessageSubCommands(event)
        } else {
            event.textChannel.sendMessage("You must use a NSFW channel for this").queue()
        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        if (event.textChannel.isNSFW) {
            handleSubCommands(event)
        } else {
            event.reply("You must use a NSFW channel for this").queue()
        }
    }


}