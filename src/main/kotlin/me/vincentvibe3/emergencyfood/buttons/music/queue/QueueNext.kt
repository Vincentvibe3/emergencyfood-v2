package me.vincentvibe3.emergencyfood.buttons.music.queue

import me.vincentvibe3.emergencyfood.commands.music.Queue
import me.vincentvibe3.emergencyfood.utils.InteractionButton
import me.vincentvibe3.emergencyfood.utils.Templates
import me.vincentvibe3.emergencyfood.utils.audio.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object QueueNext: InteractionButton() {
    override val name = "QueueNext"

    override val button = Button.primary(name, "Next")

    override fun handle(event: ButtonClickEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val queue = player.getQueue()
            val currentLastPage = queue.size/5+1
            val oldFooter = event.message.embeds[0].footer.toString()
            val pagesInfo = oldFooter.removePrefix("Page ").split("/")
            val currentPage = pagesInfo[0].toInt()
            var embedBuilder = Templates.musicQueueEmbed
            val next = if (currentLastPage<=currentPage){
                currentLastPage
            } else {
                currentPage+1
            }
            embedBuilder = embedBuilder.setFooter("Page $next/$currentLastPage")
            for (index in (next*5-1) until (next+1)*5){
                val track = queue.elementAt(index)
                val songLength = track.info.length
                val duration = Queue.getDurationFormatted(songLength)
                embedBuilder = embedBuilder
                    .addField("${index+1}", "[${track.info.title}](${track.info.uri})  ($duration)", false)
            }
            val buttons = Queue.getButtonsRow(next, currentLastPage)
            val message = MessageBuilder()
                .setEmbeds(embedBuilder.build())
                .setActionRows(buttons)
                .build()
            event.message.editMessage(message).override(true).queue()
            event.reply("Updated queue").queue()
            event.hook.deleteOriginal().queue()
        }

    }
}