package io.github.vincentvibe3.emergencyfood.buttons.music.queue

import io.github.vincentvibe3.emergencyfood.commands.music.Queue
import io.github.vincentvibe3.emergencyfood.internals.InteractionButton
import io.github.vincentvibe3.emergencyfood.utils.Templates
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button

object QueueStart: InteractionButton() {

    override val name = "QueueStart"

    override var button = Button.secondary(name, "First")

    override suspend fun handle(event: ButtonClickEvent) {
        val guildId = event.guild?.id
        val player = guildId?.let { PlayerManager.getPlayer(it) }
        if (player != null) {
            val queue = player.getQueue()
            val firstPage = 1
            if (queue.isEmpty()) {
                event.message.editMessage("The queue is empty").override(true).queue()
            } else {
                val embedBuilder = Templates.getMusicQueueEmbed()
                Queue.getEmbed(player, firstPage, embedBuilder)
                val buttons = Queue.getButtonsRow(firstPage, firstPage)
                val message = MessageBuilder()
                    .setEmbeds(embedBuilder.build())
                    .setActionRows(buttons)
                    .build()
                event.message.editMessage(message).override(true).queue()
            }
            event.reply("Updated queue").queue()
            event.hook.deleteOriginal().queue()
        }
    }

}