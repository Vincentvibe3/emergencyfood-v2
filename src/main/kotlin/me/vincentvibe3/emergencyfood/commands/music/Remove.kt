package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Remove:SlashCommand {
    override val name = "remove"

    override val command = CommandData(name, "Remove a song from the queue")
        .addOption(OptionType.INTEGER ,"index", "position in the queue", true)

    override fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val index = event.getOption("index")?.asLong?.toInt()?.minus(1)
            val queue = player.getQueue()
            if (player.getCurrentSongIndex()==index){
                player.stop()
            }
            if (queue.remove(index?.let { queue.elementAt(it) })){
                event.reply("Removed Song").queue()
            } else {
                event.reply("Failed to remove song").queue()
            }
        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }
}