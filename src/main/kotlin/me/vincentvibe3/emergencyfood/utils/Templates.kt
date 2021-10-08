package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

object Templates {
    val musicEmbedColor = Color(176, 0, 50)
    val musicEmbed = EmbedBuilder()
        .setColor(musicEmbedColor)
    val musicQueueEmbed = musicEmbed
        .setTitle("Queue")
}