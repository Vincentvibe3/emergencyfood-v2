package me.vincentvibe3.emergencyfood.utils.events

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object VoiceStateListener:ListenerAdapter() {

    //check if the bot is alone
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val channel = event.channelLeft
        val guild = event.guild
        val guildId = guild.id
        if (!event.member.user.isBot) {
            if (channel == null) {
                PlayerManager.unsetForCleanup(guildId)
            } else {
                if (channel.members.none { !it.user.isBot } && channel.members.contains(selfMember)){
                    PlayerManager.setForCleanup(guildId)
                }
            }
        } else {
            if (channel!=null){
                PlayerManager.removePlayer(guildId)
            }
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