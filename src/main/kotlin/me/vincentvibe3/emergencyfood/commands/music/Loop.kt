package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Loop:SlashCommand {
    override val name = "loop"
    override val command = CommandData(name, "toggles looping the queue")

    override fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val embed = if (player.toggleLoop()){
                EmbedBuilder()
                    .setTitle("Looping is enabled")
                    .setColor(ConfigData.musicEmbedColor)
                    .build()
            } else {
                EmbedBuilder()
                    .setTitle("Looping is disabled")
                    .setColor(ConfigData.musicEmbedColor)
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
}