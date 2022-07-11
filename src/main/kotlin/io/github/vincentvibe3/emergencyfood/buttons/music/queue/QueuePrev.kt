package io.github.vincentvibe3.emergencyfood.buttons.music.queue

import io.github.vincentvibe3.emergencyfood.commands.music.Queue
import io.github.vincentvibe3.emergencyfood.internals.InteractionButton
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

object QueuePrev : InteractionButton() {

    override val name = "QueuePrev"

    override var button = Button.primary(name, "Prev")

    override suspend fun handle(event: ButtonInteractionEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val queue = player.getQueue()
            val currentLastPage = Queue.getPageCount(queue)
            val oldFooter = event.message.embeds[0].footer?.text
            val pagesInfo = oldFooter?.removePrefix("Page ")?.split("/")
            if (queue.isEmpty()) {
                event.message.editMessage("The queue is empty").override(true).queue()
            } else {
                if (pagesInfo != null) {
                    val currentPage = pagesInfo[0].toInt()
                    val embedBuilder = Templates.getMusicQueueEmbed()
                    val prev = if (currentLastPage < currentPage) {
                        currentLastPage
                    } else {
                        currentPage - 1
                    }
                    Queue.getEmbed(player, prev, embedBuilder)
                    val buttons = Queue.getButtonsRow(prev, currentLastPage)
                    val message = MessageBuilder()
                        .setEmbeds(embedBuilder.build())
                        .setActionRows(buttons)
                        .build()
                    event.message.editMessage(message).override(true).queue()
                }
            }
            event.reply("Updated queue").queue()
            event.hook.deleteOriginal().queue()
        }


    }

}