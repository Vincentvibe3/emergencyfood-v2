package io.github.vincentvibe3.emergencyfood.internals.events

import io.github.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ReadyListener : ListenerAdapter() {

    // display info when bot is logged in
    override fun onReady(event: ReadyEvent) {
        Logging.logger.info("Logged in as ${event.jda.selfUser.name}#${event.jda.selfUser.discriminator}")
        Logging.logger.info("Active in ${event.jda.guilds.size} guilds")

    }

}