package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Shuffle: GenericCommand(), SlashCommand, MessageCommand {

    override val name = "shuffle"

    override val command = CommandData(name, "Shuffle the queue")

    override suspend fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isQueueEmpty()){
                event.reply("Cannot shuffle an empty queue").queue()
            } else {
                player.shuffle()
                val embed = Templates.getMusicEmbed()
                    .setTitle("Shuffled queue")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .build()
                event.reply(message).queue()
            }
        } else {
            event.reply("Failed to fetch player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val channel = event.textChannel
        if (player.isQueueEmpty()){
            channel.sendMessage("Cannot shuffle an empty queue").queue()
        } else {
            player.shuffle()
            val embed = Templates.getMusicEmbed()
                .setTitle("Shuffled queue")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        }
    }
}