package io.github.vincentvibe3.emergencyfood.commands.kana

import io.github.vincentvibe3.emergencyfood.internals.MessageResponse
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

open class KanaMessageResponse(override val user: String, override val channel: String, private val answer: String) :
    MessageResponse {

    override fun handle(event: MessageReceivedEvent) {
        if (event.message.contentDisplay.lowercase() == answer) {
            event.channel.sendMessage("You answered correctly").queue()
        } else {
            event.channel.sendMessage("The answer was `$answer`").queue()
        }
    }

}