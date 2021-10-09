package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

interface SubCommand {

    fun handle(event: SlashCommandEvent)

}