package io.github.vincentvibe3.emergencyfood.commands.music

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.GenericCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageCommand
import io.github.vincentvibe3.emergencyfood.internals.SlashCommand
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.CommonPlayer
import io.github.vincentvibe3.emergencyfood.utils.audio.common.LoadResult
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import io.github.vincentvibe3.emergencyfood.utils.exceptions.LoadFailedException
import io.github.vincentvibe3.emergencyfood.utils.exceptions.QueueAddException
import io.github.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

object Play : GenericCommand(), SlashCommand, MessageCommand {

    override val name = "play"

    override val command = Commands.slash(name, "Play a song or resume playback")
        .addOption(OptionType.STRING, "song", "link or search query", false)

    //determine whether the song is a YouTube url or a search query
    private fun getTrack(query: String): String {
        val isVideo = query.startsWith("https://www.youtube.com/watch?v=")
        val isPlaylist = query.startsWith("https://www.youtube.com/playlist?list=")
        val isMobile = query.startsWith("https://youtu.be/")
        return if (isVideo || isMobile || isPlaylist) {
            if (isVideo && query.contains("&list=")) {
                query.replaceAfter("&list=", "").replace("&list=", "")
            } else {
                query
            }
        } else {
            query
//            SongSearch.getSong(query)
        }
    }

    //connect to the vc
    private fun connect(channel: AudioChannel, player: CommonPlayer) {
        val guild = channel.guild
        val audioManager = guild.audioManager
        audioManager.sendingHandler = player.getAudioHandler()
        try {
            audioManager.openAudioConnection(channel)
        } catch (e: InsufficientPermissionException) {
            val messageChannel = Bot.getClientInstance().getTextChannelById(player.getUpdateChannel())
            messageChannel?.sendMessage("Could not connect to channel")
        }

    }


    //wait for playlist to be loaded,
    //allows the song added to queue count to be accurate
    private suspend fun waitForPlaylistLoad(player: CommonPlayer, initSize: Int) {
        coroutineScope {
            val job = launch {
                while (!player.isPlaying() || player.getQueue().size == initSize) {
                    delay(100L)
                }
            }
            job.join()
        }
    }

    private suspend fun waitForLoad(player: CommonPlayer, loadResult: LoadResult) {
        coroutineScope {
            val job = launch {
                while (!player.isPlaying() || loadResult.value == -1) {
                    delay(100L)
                }
            }
            job.join()
        }
    }

    //change mobile links to be readable by lavaplayer
    private fun formatMobileLinks(url: String): String {
        return url.replace("https://youtu.be/", "https://www.youtube.com/watch?v=")
    }

    //resume and return response depending on whether the player was resumed
    private fun resume(player: CommonPlayer): Message {
        return if (player.isPaused()) {
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
    private suspend fun play(player: CommonPlayer, query: String): Message {
        lateinit var track: String
        val initSize = player.getQueue().size
        lateinit var loadResult: LoadResult

        try {
            track = getTrack(query)
            if (!track.startsWith("https://www.youtube.com/playlist?list=")) {
                loadResult = LoadResult()
                player.loadQueue.put(loadResult)
            }
            player.play(track)
        } catch (e: SongNotFoundException) {
            return MessageBuilder()
                .setContent("Could not find a matching song")
                .build()
        } catch (e: LoadFailedException) {
            return MessageBuilder()
                .setContent("Failed to load song")
                .build()
        } catch (e: QueueAddException) {
            return MessageBuilder()
                .setContent("An error occurred while adding the song to the queue")
                .build()
        }
        val embed = if (track.startsWith("https://www.youtube.com/playlist?list=")||track.startsWith("https://open.spotify.com/playlist/")) {
            waitForPlaylistLoad(player, initSize)
            Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added ${player.getQueue().size - initSize} songs from [playlist]($track)")
                .build()
        } else {
            waitForLoad(player, loadResult)
            Templates.getMusicEmbed()
                .setTitle("Queued")
                .setDescription("Added [${player.getSongTitle(loadResult.value)}](${player.getSongUrl(loadResult.value)})")
                .build()
        }
        return MessageBuilder()
            .setEmbeds(embed)
            .build()

    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        val channel = event.member?.voiceState?.channel
        try {
            event.member?.deafen(true)
        } catch (e: InsufficientPermissionException) {
            Logging.logger.debug("Failed to self deafen")
        } catch (e: IllegalStateException) {
            Logging.logger.debug("Failed to self deafen")
        }
        val songOption = event.getOption("song")?.asString
        if (player != null && channel != null) {
            player.setUpdateChannel(event.guildChannel.id)
            connect(channel, player)
            val response = if (songOption == null) {
                resume(player)
            } else {
                play(player, songOption)
            }
            event.hook.editOriginal(response).queue()
        } else if (player == null) {
            event.hook.editOriginal("An error occurred when fetching the player").queue()
        } else if (channel == null) {
            event.hook.editOriginal("You must join a voice channel to play").queue()
        }
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val guildId = event.guild.id
        val player = guildId.let { PlayerManager.getPlayer(it) }
        val channel = event.member?.voiceState?.channel
        val textChannel = event.guildChannel
        try {
            event.member?.deafen(true)
        } catch (e: InsufficientPermissionException) {
            Logging.logger.debug("Failed to self deafen")
        } catch (e: IllegalStateException) {
            Logging.logger.debug("Failed to self deafen")
        }
        val options = event.getOptions()
        val songOption = if (options.isEmpty()) {
            null
        } else {
            var song = ""
            options.forEach { song += " $it" }
            song
        }
        if (channel != null) {
            player.setUpdateChannel(event.guildChannel.id)
            connect(channel, player)
            val response = if (songOption == null) {
                resume(player)
            } else {
                play(player, songOption)
            }
            textChannel.sendMessage(response).queue()
        } else {
            textChannel.sendMessage("You must join a voice channel to play").queue()
        }
    }
}
