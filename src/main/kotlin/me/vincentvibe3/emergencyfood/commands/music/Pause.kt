package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Pause:SlashCommand {
    override val name = "pause"
    override val command = CommandData(name, "pause playback")

    override fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null){
            if (player.isPlaying() && !player.isPaused()){
                val track = player.getCurrentSongTitle()
                player.pause()
                event.reply("Paused $track").queue()
            } else {
                event.reply("No track is playing").queue()
            }
        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }
}