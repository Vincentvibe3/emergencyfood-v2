package io.github.vincentvibe3.emergencyfood.internals.events

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.Logging
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object VoiceStateListener: ListenerAdapter() {

    //check if the bot is alone
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val channelLeft = event.channelLeft
        val channelJoin = event.channelJoined
        val guild = event.guild
        val guildId = guild.id
        if (!event.member.user.isBot) {
            if (channelJoin != null && channelJoin.members.contains(selfMember)) {
                Logging.logger.debug("User Connected to vc")
                PlayerManager.unsetForCleanup(guildId)
            } else if (channelLeft!=null && channelLeft.members.contains(selfMember)){
                Logging.logger.debug("User Disconnected from vc")
                if (channelLeft.members.none { !it.user.isBot } ){
                    PlayerManager.setForCleanup(guildId)
                }
            }
        } else {
            if (event.member.user.id == selfId && channelJoin != null){
                if (channelJoin.members.none { !it.user.isBot }){
                    PlayerManager.setForCleanup(guildId)
                }
            } else if (event.member.user.id == selfId && channelLeft != null && !PlayerManager.isSetForCleanup(guildId)){
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
                val messageChannel = player.getAnnouncementChannel()
                val client = Bot.getClientInstance()
                client.getTextChannelById(messageChannel)?.sendMessage("Please do not server undeafen the bot")?.queue()
            }
        }
    }

}