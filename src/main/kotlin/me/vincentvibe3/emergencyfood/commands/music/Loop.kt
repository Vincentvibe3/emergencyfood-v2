package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.internals.GenericCommand
import me.vincentvibe3.emergencyfood.internals.MessageCommand
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Loop: GenericCommand(), SlashCommand, MessageCommand {

    override val name = "loop"

    override val command = CommandData(name, "Toggles looping the queue")

    override suspend fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val embed = if (player.toggleLoop()){
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
        val embed = if (player.toggleLoop()){
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