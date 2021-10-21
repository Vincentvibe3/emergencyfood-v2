package me.vincentvibe3.emergencyfood.commands.music

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.utils.Logging
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.*
import me.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
import me.vincentvibe3.emergencyfood.utils.exceptions.QueueAddException
import me.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

object Play: SlashCommand() {

    override val name = "play"


    override val command = CommandData(name, "Play a song or resume playback")
        .addOption(OptionType.STRING ,"song", "link or search query", false)

    //determine whether the song is a YouTube url or a search query
    private fun getTrack(query:String):String{
        val isVideo = query.startsWith("https://www.youtube.com/watch?v=")
        val isPlaylist = query.startsWith("https://www.youtube.com/playlist?list=")
        val isMobile = query.startsWith("https://youtu.be/")
        return if (isVideo||isMobile||isPlaylist){
            if (isVideo&&query.contains("&list=")){
                query.replaceAfter("&list=", "").replace("&list=", "")
            } else {
                query
            }
        } else {
            SongSearch.getSong(query)
        }
    }

    //connect to the vc
    private fun connect(channel:VoiceChannel, player: Player){
        val guild = channel.guild
        val audioManager = guild.audioManager
        audioManager.sendingHandler = player.getAudioHandler()
        audioManager.openAudioConnection(channel)
    }


    //wait for the added song to load
    //allows the queued embed to display the song title without issues
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

    //wait for playlist to be loaded,
    //allows the song added to queue count to be accurate
    private fun waitForPlaylistLoad(player: Player, initSize:Int){
        runBlocking {
            val job = launch {
                while (!player.isPlaying() || player.getQueue().size == initSize){
                    delay(100L)
                }
            }
            job.join()
        }
    }

    //change mobile links to be readable by lavaplayer
    private fun formatMobileLinks(url: String):String{
        return url.replace("https://youtu.be/", "https://www.youtube.com/watch?v=")
    }

    //resume and return response depending on whether the player was resumed
    private fun resume(player:Player): Message{
        return if (player.isPaused()){
            player.resume()
            val embed = Templates.getMusicEmbed()
                .setTitle("Resumed Playback")
                .build()
            MessageBuilder()
                .setEmbeds(embed)
                .build()
        } else {
            MessageBuilder()
                .setContent("The player is not paused")
                .build()
        }
    }

    //plays and return response depending on whether loading was successful or not
    private fun play(player: Player, query:String):Message{
        lateinit var track:String
        val initSize = player.getQueue().size
        try {
            track = getTrack(query)
            player.play(track)
        } catch (e: SongNotFoundException){
            return MessageBuilder()
                .setContent("Could not find a matching song")
                .build()
        }catch (e: LoadFailedException){
            return MessageBuilder()
                .setContent("Failed to load song")
                .build()
        } catch (e: QueueAddException){
            return MessageBuilder()
                .setContent("An error occurred while adding the song to the queue")
                .build()
        }
        val embed = if (track.startsWith("https://www.youtube.com/playlist?list=")){
            waitForPlaylistLoad(player, initSize)
            Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added ${player.getQueue().size-initSize} songs from [playlist]($track)")
                .build()
        } else {
            waitForLoad(player, track)
            Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added [${player.getLastSongTitle()}](${player.getLastSongUrl()})")
                .build()
        }
        return MessageBuilder()
            .setEmbeds(embed)
            .build()

    }

    override fun handle(event: SlashCommandEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        val channel = event.member?.voiceState?.channel
        try {
            event.member?.deafen(true)
        } catch (e: InsufficientPermissionException){
            Logging.logger.debug("Failed to self deafen")
        } catch (e:IllegalStateException){
            Logging.logger.debug("Failed to self deafen")
        }
        val songOption = event.getOption("song")?.asString
        if (player != null && channel != null) {
            player.setUpdateChannel(event.textChannel.id)
            connect(channel, player)
            val response = if (songOption == null) {
                resume(player)
            } else {
                play(player, songOption)
            }
            event.hook.editOriginal(response).queue()
        } else if (player == null){
            event.hook.editOriginal("An error occurred when fetching the player").queue()
        } else if (channel == null){
            event.hook.editOriginal("You must join a voice channel to play").queue()
        }
    }
}
