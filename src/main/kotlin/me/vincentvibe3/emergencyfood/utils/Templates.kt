package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

object Templates {
    private val musicEmbedColor = Color(176, 0, 50)
    private val sauceEmbedColor = Color(0,141,163)
    //requests per second
    private val defaultRateLimit  = 5

    fun getMusicEmbed():EmbedBuilder{
        return EmbedBuilder()
            .setColor(musicEmbedColor)
    }

    fun getMusicQueueEmbed():EmbedBuilder{
        return getMusicEmbed()
            .setTitle("Queue")
    }

    fun getSauceEmbed():EmbedBuilder{
        return EmbedBuilder()
            .setColor(sauceEmbedColor)
    }
}