package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Disconnect:SlashCommand {
    override val name = "disconnect"
    override val command = CommandData(name, "Disconnects from the voice channel")

    override fun handle(event: SlashCommandEvent) {
        val guild = event.guild
        if (guild != null) {
            if (guild.audioManager.isConnected){
                guild.audioManager.closeAudioConnection()
                PlayerManager.removePlayer(guild.id)
                val embed = EmbedBuilder()
                    .setTitle("Disconnected")
                    .setColor(ConfigData.musicEmbedColor)
                    .build()
                val message = MessageBuilder()
                    .setEmbeds(embed)
                    .build()
                event.reply(message).queue()
            } else {
                event.reply("Cannot disconnect, The bot is not connected to a voice channel").queue()
            }
        } else {
            event.reply("Could not fetch the required server").queue()
        }
    }
}