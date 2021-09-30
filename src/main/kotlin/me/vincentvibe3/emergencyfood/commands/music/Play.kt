package me.vincentvibe3.emergencyfood.commands.music

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.Player
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import me.vincentvibe3.emergencyfood.utils.audio.SongSearch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.util.concurrent.TimeUnit

class Play:SlashCommand {

    override val name = "play"

    @Bot.Beta
    override val command = CommandData(name, "play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    private fun getTrack(query:String):String{
        return if (query.startsWith("https://www.youtube.com/watch?v=")||query.startsWith("https://youtu.be/")){
            query
        } else {
            SongSearch.getSong(query)
        }
    }

    private fun connect(channel:VoiceChannel, player: Player){
        val guild = channel.guild
        val audioManager = guild.audioManager
        audioManager.sendingHandler = player.getAudioHandler()
        audioManager.openAudioConnection(channel)
    }

    private fun waitForLoad(player: Player, track:String){
        val url = formatMobileLinks(track)
        runBlocking {
            val job = launch {
                while (!player.isPlaying() || player.getLastSongUrl() != url){
                    delay(100L)
                }
            }
            job.join()
        }
    }


    private fun formatMobileLinks(url: String):String{
        return url.replace("https://youtu.be/", "https://www.youtube.com/watch?v=")
    }

    private fun resume(player:Player): Message{
        return if (player.isPaused()){
            player.resume()
            val embed = EmbedBuilder().setTitle("Resumed Playback").build()
            MessageBuilder()
                .setEmbeds(embed)
                .build()
        } else {
            MessageBuilder()
                .setContent("The player is not paused")
                .build()
        }
    }

    private fun play(player: Player, track:String):Message{
        player.play(track)
        waitForLoad(player, track)
        val embed = EmbedBuilder()
            .setTitle("Queued")
            .setDescription("Added [${player.getLastSongTitle()}](${player.getLastSongUrl()})")
            .build()
        return MessageBuilder()
            .setEmbeds(embed)
            .build()

    }

    private fun respond(event:SlashCommandEvent, message:String, ephemeral:Boolean){
        event.hook.setEphemeral(ephemeral)
        event.hook.editOriginal(message).queue()
    }

    private fun respond(event:SlashCommandEvent, message:Message, ephemeral:Boolean){
        event.hook.setEphemeral(ephemeral)
        event.hook.editOriginal(message).queue()
    }

    override fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        val channel = event.member?.voiceState?.channel
        val songOption = event.getOption("song")?.asString
        if (player != null) {
            channel?.let{ connect(channel, player) }
            val response = if (songOption == null){
                resume(player)
            } else {
                val track = getTrack(songOption)
                play(player, track)
            }
            respond(event, response, false)
        } else {
            respond(event, "An error occurred when fetching the player", true)
        }
    }
}