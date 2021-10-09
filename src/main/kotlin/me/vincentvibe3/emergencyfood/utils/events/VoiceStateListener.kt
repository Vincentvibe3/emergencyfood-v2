package me.vincentvibe3.emergencyfood.utils.events

import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object VoiceStateListener:ListenerAdapter() {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.channelLeft==null){

        } else {
            val guild = event.guild.id
            PlayerManager.removePlayer(guild)
        }
    }

    override fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {
        if (event.member.id == Bot.getClientInstance().selfUser.id){
            if (!event.isGuildDeafened){
                event.member.deafen(true).queue()
                val guild = event.guild.id
                val player = PlayerManager.getPlayer(guild)
                if (player != null) {
                    val messageChannel = player.getAnnouncementChannel()
                    val client = Bot.getClientInstance()
                    client.getTextChannelById(messageChannel)?.sendMessage("Please do not server undeafen the bot")?.queue()
                }
            }
        }
    }

}