package io.github.vincentvibe3.emergencyfood.commands.misc

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Divorce:GenericCommand(), SlashCommand, MessageCommand {
    override val name: String = "divorce"

    override suspend fun handle(event: MessageReceivedEvent) {
        val p1 = event.message.mentions.members[0]
        val p2 = event.message.mentions.members[1]
        if (p1 != null && p2 != null) {
            event.channel.asTextChannel().sendMessage(
                ":broken_heart: ${p1.asMention} is now divorced from ${p2.asMention} :broken_heart:"
            ).queue()
        }
    }

    override val command: CommandData = Commands.slash(name, "Basically a divorce certificate")
        .addOption(OptionType.MENTIONABLE, "person_one", "the username of the first person")
        .addOption(OptionType.MENTIONABLE, "person_two", "the username of the second person")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val p1 = event.getOption("person_one")
        val p2 = event.getOption("person_two")
        if (p1 != null && p2 != null) {
            event.reply(
                ":broken_heart: ${p1.asMember?.asMention} is now divorced from ${p2.asMember?.asMention} :broken_heart:"
            ).queue()
        }
    }
}