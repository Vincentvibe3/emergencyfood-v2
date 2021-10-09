package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Skip: SlashCommand() {

    override val name = "skip"

    override val command = CommandData(name, "Skip the currently playing song")

    override fun handle(event: SlashCommandEvent) {
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
                event.reply(message).queue()
            }
        } else {
            event.reply("Failed to fetch player").queue()
        }
    }
}