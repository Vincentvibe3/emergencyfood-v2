package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.internals.GenericCommand
import me.vincentvibe3.emergencyfood.internals.MessageCommand
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Remove: GenericCommand(), SlashCommand, MessageCommand {

    override val name = "remove"

    override val command = CommandData(name, "Remove a song from the queue")
        .addOption(OptionType.INTEGER ,"index", "position in the queue", true)

    override suspend fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val index = event.getOption("index")?.asLong?.toInt()?.minus(1)
            val queue = player.getQueue()
            if (index != null&&index>=0&&index<queue.size) {
                if (player.getCurrentSongIndex()==index){
                    player.stop()
                }
                if (queue.remove(index.let { queue.elementAt(it) })){
                    event.reply("Removed Song").queue()
                } else {
                    event.reply("Failed to remove song").queue()
                }
            } else {
                event.reply("Enter a valid index").queue()
            }

        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val channel = event.textChannel
        val options = event.getOptions()
        val index = if (options.isEmpty()){
            null
        } else {
            options[0].toIntOrNull()
        }
        val queue = player.getQueue()
        if (index != null&&index>=0&&index<queue.size) {
            if (player.getCurrentSongIndex()==index){
                player.stop()
            }
            if (queue.remove(index.let { queue.elementAt(it) })){
                channel.sendMessage("Removed Song").queue()
            } else {
                channel.sendMessage("Failed to remove song").queue()
            }
        } else {
            channel.sendMessage("Enter a valid index").queue()
        }

    }
}