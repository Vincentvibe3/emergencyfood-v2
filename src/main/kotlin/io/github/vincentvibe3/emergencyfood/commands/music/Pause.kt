package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder

object Pause : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "pause"

    override val command = Commands.slash(name, "Pause playback")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isPlaying() && !player.isPaused()) {
                player.pause()
                val embed = Templates.getMusicEmbed()
                    .setTitle("Paused")
                    .build()
                val message = MessageCreateBuilder()
                    .setEmbeds(embed)
                    .build()
                event.reply(message).queue()
            } else {
                event.reply("No track is playing").queue()
            }
        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        if (player.isPlaying() && !player.isPaused()) {
            player.pause()
            val embed = Templates.getMusicEmbed()
                .setTitle("Paused")
                .build()
            val message = MessageCreateBuilder()
                .setEmbeds(embed)
                .build()
            event.guildChannel.sendMessage(message).queue()
        } else {
            event.guildChannel.sendMessage("No track is playing").queue()
        }
    }
}