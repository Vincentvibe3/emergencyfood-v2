package io.github.vincentvibe3.emergencyfood.commands.misc

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Roll : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "roll"

    override val command = Commands.slash(name, "rolls a number between 0 and 100")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val number = (0..100).random()
        event.reply("You rolled $number").queue()
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val channel = event.textChannel
        val number = (0..100).random()
        channel.sendMessage("You rolled $number").queue()
    }

}