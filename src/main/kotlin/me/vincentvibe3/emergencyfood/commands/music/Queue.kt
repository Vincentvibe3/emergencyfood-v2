package me.vincentvibe3.emergencyfood.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueNext
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueuePrev
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueStart
import me.vincentvibe3.emergencyfood.internals.ButtonManager
import me.vincentvibe3.emergencyfood.internals.GenericCommand
import me.vincentvibe3.emergencyfood.internals.SlashCommand
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.Player
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import java.util.concurrent.BlockingQueue

object Queue: GenericCommand(), SlashCommand {

    init {
        ButtonManager.registerLocal(QueueNext)
        ButtonManager.registerLocal(QueueEnd)
        ButtonManager.registerLocal(QueueStart)
        ButtonManager.registerLocal(QueuePrev)
    }

    override val name = "queue"

    override val command = CommandData(name, "Displays the active queue")

    private fun getDurationFormatted(length:Long): String{
        val hours = length / 1000 / 3600
        val minutes = length / 1000 / 60 % 60
        val seconds = length / 1000 % 60
        var formattedTime = ""
        if (hours > 0){
            formattedTime+="$hours:"
        }
        if (minutes > 0){
            if (minutes.toString().length < 2){
                formattedTime+="0"
            }
            formattedTime+="$minutes:"
        }
        if (minutes == 0L){
            formattedTime+="00:"
        }
        if (seconds.toString().length < 2){
            formattedTime+="0"
        }
        formattedTime+="$seconds"
        return formattedTime
    }

    fun getButtonsRow(currentPage:Int, lastPage:Int):ActionRow{
        return if (currentPage==lastPage&&lastPage!=1){
            ActionRow.of(
                QueueStart.getEnabled(),
                QueuePrev.getEnabled(),
                QueueNext.getDisabled(),
                QueueEnd.getDisabled()
            )
        } else if (currentPage==1){
            ActionRow.of(
                QueueStart.getDisabled(),
                QueuePrev.getDisabled(),
                QueueNext.getEnabled(),
                QueueEnd.getEnabled()
            )
        } else {
            ActionRow.of(
                QueueStart.getEnabled(),
                QueuePrev.getEnabled(),
                QueueNext.getEnabled(),
                QueueEnd.getEnabled()
            )
        }

    }

    fun getPageCount(queue: BlockingQueue<AudioTrack>):Int{
        return if (queue.size%5 == 0){
            queue.size/5
        } else {
            queue.size/5+1
        }
    }

    fun getEmbed(player:Player, page:Int, embedBuilder:EmbedBuilder):EmbedBuilder{
        val queue = player.getQueue()
        var embed = embedBuilder
        val start = (page-1)*5
        val end = if (page*5-1>queue.size-1){
            queue.size-1
        } else {
            page*5-1
        }
        embed = embed.setFooter("Page $page/${getPageCount(queue)}")
        val currentSong = queue.indexOf(player.getCurrentSong())
        for (index in start..end) {
            val track = queue.elementAt(index)
            val songLength = track.info.length
            val duration = getDurationFormatted(songLength)
            val currentMessage = if (index==currentSong){
                "(Now Playing)"
            } else {
                ""
            }
            embed = embed
                .addField("${index+1} $currentMessage", "[${track.info.title}](${track.info.uri})  ($duration)", false)
        }
        return embed
    }

    override suspend fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        var embedBuilder = Templates.getMusicQueueEmbed()
        if (player != null) {
            val queue = player.getQueue()
            if (queue.isEmpty()){
                event.reply("The queue is empty").queue()
            } else {
                val lastPage = getPageCount(queue)
                embedBuilder = embedBuilder.setFooter("Page 1/$lastPage")
                getEmbed(player, 1, embedBuilder)
                val buttons = getButtonsRow(1, lastPage)
                val message = MessageBuilder()
                    .setEmbeds(embedBuilder.build())
                    .setActionRows(buttons)
                    .build()
                event.reply(message).queue()
            }
        } else {
            event.reply("An error occurred when fetching the player").queue()
        }
    }
}