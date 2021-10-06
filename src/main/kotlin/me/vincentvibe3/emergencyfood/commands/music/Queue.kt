package me.vincentvibe3.emergencyfood.commands.music

import me.vincentvibe3.emergencyfood.utils.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Queue:SlashCommand {
    override val name = "queue"

    override val command = CommandData(name, "Displays the active queue")

    override fun handle(event: SlashCommandEvent) {
        TODO("Not yet implemented")
    }
}