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

object NowPlaying : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "nowplaying"

    override val command = Commands.slash(name, "Get the current song name")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isPlaying()) {
                val embed = Templates.getMusicEmbed()
                    .setTitle("Now Playing")
                    .setDescription("[${player.getCurrentSongTitle()}](${player.getCurrentSongUrl()})")
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .build()
                event.reply(message).queue()
            } else {
                event.reply("Nothing is currently playing").queue()
            }
        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val channel = event.textChannel
        if (player.isPlaying()) {
            val embed = Templates.getMusicEmbed()
                .setTitle("Now Playing")
                .setDescription("[${player.getCurrentSongTitle()}](${player.getCurrentSongUrl()})")
                .build()
            val message = MessageBuilder()
                .setEmbeds(embed)
                .build()
            channel.sendMessage(message).queue()
        } else {
            channel.sendMessage("Nothing is currently playing").queue()
        }
    }
}