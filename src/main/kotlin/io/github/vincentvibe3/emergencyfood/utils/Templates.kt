package io.github.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.EmbedBuilder

object Templates {
    private const val musicEmbedColor = 0xb00032//Color(176, 0, 50)
    private const val sauceEmbedColor = 0x008da3//Color(0,141,163)
    //requests per second
    const val defaultRateLimit = 5L

    fun getMusicEmbed(): EmbedBuilder {
        return EmbedBuilder()
            .setColor(musicEmbedColor)
    }

    fun getMusicQueueEmbed(): EmbedBuilder {
        return getMusicEmbed()
            .setTitle("Queue")
    }

    fun getSauceEmbed(): EmbedBuilder {
        return EmbedBuilder()
            .setColor(sauceEmbedColor)
    }

    fun setRateLimits() {
        RequestHandler.rateLimits["api.github.com"] = 2L
    }
}
