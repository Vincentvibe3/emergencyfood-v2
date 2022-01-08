package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Skip:GenericCommand(), SlashCommand, MessageCommand {

    override val name = "skip"

    override val command = CommandData(name, "Skip the currently playing song")

    override suspend fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isQueueEmpty()){
                event.reply("Cannot skip, the queue is empty").queue()
            } else {
                if (player.isLastSong()&&!player.getLoop()){
                    player.clear()
                } else {
                    player.skip()
                }
                val embed = Templates.getMusicEmbed()
                    .setTitle("Skipped song")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .build()
                event.hook.editOriginal(message).queue()

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
            channel.sendMessage("Cannot skip, the queue is empty").queue()
        } else {
            if (player.isLastSong()&&!player.getLoop()){
                player.clear()
            } else {
                player.skip()
            }
            val embed = Templates.getMusicEmbed()
                .setTitle("Skipped song")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        }
    }
}