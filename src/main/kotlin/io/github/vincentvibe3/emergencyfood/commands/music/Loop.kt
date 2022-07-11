package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Loop : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "loop"

    override val command = Commands.slash(name, "Toggles looping the queue")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val embed = if (player.toggleLoop()) {
                Templates.getMusicEmbed()
                    .setTitle("Looping is enabled")
                    .build()
            } else {
                Templates.getMusicEmbed()
                    .setTitle("Looping is disabled")
                    .build()
            }
            val response = MessageBuilder()
                .setEmbeds(embed)
                .build()
            event.reply(response).queue()
        } else {
            event.reply("Failed to fetch player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val embed = if (player.toggleLoop()) {
            Templates.getMusicEmbed()
                .setTitle("Looping is enabled")
                .build()
        } else {
            Templates.getMusicEmbed()
                .setTitle("Looping is disabled")
                .build()
        }
        val response = MessageBuilder()
            .setEmbeds(embed)
            .build()
        event.textChannel.sendMessage(response).queue()
    }
}