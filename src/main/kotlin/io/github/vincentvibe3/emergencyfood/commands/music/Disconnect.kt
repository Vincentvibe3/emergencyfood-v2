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

object Disconnect : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "disconnect"

    override val command = Commands.slash(name, "Disconnects from the voice channel")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guild = event.guild
        if (guild != null) {
            if (guild.audioManager.isConnected) {
                guild.audioManager.closeAudioConnection()
                PlayerManager.removePlayer(guild.id)
                val embed = Templates.getMusicEmbed()
                    .setTitle("Disconnected")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .build()
                event.reply(message).queue()
            } else {
                event.reply("Cannot disconnect, The bot is not connected to a voice channel").queue()
            }
        } else {
            event.reply("Could not fetch the required server").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guild = event.guild
        val channel = event.textChannel
        if (guild.audioManager.isConnected) {
            guild.audioManager.closeAudioConnection()
            PlayerManager.removePlayer(guild.id)
            val embed = Templates.getMusicEmbed()
                .setTitle("Disconnected")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        } else {
            channel.sendMessage("Cannot disconnect, The bot is not connected to a voice channel").queue()
        }
    }
}