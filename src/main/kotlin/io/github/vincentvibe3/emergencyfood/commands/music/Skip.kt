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

object Skip : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "skip"

    override val command = Commands.slash(name, "Skip the currently playing song")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isQueueEmpty()) {
                event.hook.editOriginal ("Cannot skip, the queue is empty").queue()
            } else {
                if (player.isLastSong()&&!player.looped()){
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
            event.hook.editOriginal("Failed to fetch player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val channel = event.guildChannel
        if (player.isQueueEmpty()) {
            channel.sendMessage("Cannot skip, the queue is empty").queue()
        } else {
            if (player.isLastSong()&&!player.looped()){
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