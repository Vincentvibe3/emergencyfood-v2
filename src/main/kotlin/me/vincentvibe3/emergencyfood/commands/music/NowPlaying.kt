package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object NowPlaying:SlashCommand {
    override val name = "nowPlaying"

    override val command = CommandData(Pause.name, "Get the current song name")

    override fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            if (player.isPlaying()){
                val embed = EmbedBuilder()
                    .setTitle("Now Playing")
                    .setDescription("[${player.getCurrentSongTitle()}](${player.getCurrentSongUrl()})")
                    .setColor(ConfigData.musicEmbedColor)
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
}