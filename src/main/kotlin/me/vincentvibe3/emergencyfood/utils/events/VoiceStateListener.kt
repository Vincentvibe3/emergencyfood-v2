package me.vincentvibe3.emergencyfood.utils.events

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object VoiceStateListener:ListenerAdapter() {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val channel = event.channelLeft
        val guild = event.guild
        val guildId = guild.id
        if (event.member!=selfMember) {
            val player = PlayerManager.getPlayer(guildId)
            if (channel == null) {
                player?.cleanup = false
            } else {
                if (channel.members.size == 1 && channel.members.contains(selfMember)) {
                    player?.cleanup = true
                    playerCleanDelay(5)
                    val updatedPlayer = PlayerManager.getPlayer(guildId)
                    if (updatedPlayer?.cleanup == true) {
                        val messageChannel = updatedPlayer.getAnnouncementChannel()
                        val embed = Templates.getMusicEmbed()
                            .setTitle("Disconnected due to inactivity")
                            .build()
                        val message = MessageBuilder()
                            .setEmbeds(embed)
                            .build()
                        client.getTextChannelById(messageChannel)?.sendMessage(message)?.queue()
                        PlayerManager.removePlayer(guildId)
                        guild.audioManager.closeAudioConnection()
                    }
                }
            }
        } else {
            if (channel!=null){
                PlayerManager.removePlayer(guildId)
            }
        }
    }

    //delay set in minutes
    private fun playerCleanDelay(delay:Long){
        val delayInMillis = delay*60*1000
        runBlocking{
            val job = launch {
                delay(delayInMillis)
            }
            job.join()
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