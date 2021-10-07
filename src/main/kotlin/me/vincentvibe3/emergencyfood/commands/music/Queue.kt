package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueEnd
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueNext
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueuePrev
import me.vincentvibe3.emergencyfood.buttons.music.queue.QueueStart
import me.vincentvibe3.emergencyfood.utils.ButtonManager
import me.vincentvibe3.emergencyfood.utils.ConfigData
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.components.ActionRow

object Queue:SlashCommand {

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

    override fun handle(event: SlashCommandEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        var embedBuilder = EmbedBuilder()
            .setTitle("Queue")
            .setColor(ConfigData.musicEmbedColor)
        if (player != null) {
            val queue = player.getQueue()
            if (queue.isEmpty()){
                event.reply("The queue is empty").queue()
            } else {
                val end = if (queue.size < 5){
                    queue.size-1
                } else {
                    embedBuilder = embedBuilder.setFooter("and ${queue.size-5} more")
                    4
                }
                for (index in 0..end){
                    val track = queue.elementAt(index)
                    val songLength = track.info.length
                    val duration = getDurationFormatted(songLength)
                    embedBuilder = embedBuilder
                        .addField("${index+1}", "[${track.info.title}](${track.info.uri})  ($duration)", false)
                }
                val buttons = ActionRow.of(
                    QueueStart.getDisabled(),
                    QueuePrev.getDisabled(),
                    QueueNext.getEnabled(),
                    QueueEnd.getDisabled()
                )
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