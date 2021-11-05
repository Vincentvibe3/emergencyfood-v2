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

object Clear: GenericCommand(), SlashCommand, MessageCommand {

    override val name = "clear"

    override val command = CommandData(name, "Clears the queue")

    override suspend fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isQueueEmpty()){
                event.reply("Cannot clear, the queue is already empty").queue()
            } else {
                player.clear()
                val embed = Templates.getMusicEmbed()
                    .setTitle("Cleared queue")
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
        val channel = event.textChannel
        val player = guildId.let { PlayerManager.getPlayer(it) }
        if (player.isQueueEmpty()){
            channel.sendMessage("Cannot clear, the queue is already empty").queue()
        } else {
            player.clear()
            val embed = Templates.getMusicEmbed()
                .setTitle("Cleared queue")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        }
    }

}