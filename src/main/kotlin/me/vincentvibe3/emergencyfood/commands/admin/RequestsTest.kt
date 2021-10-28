package me.vincentvibe3.emergencyfood.commands.admin

import kotlinx.coroutines.*
import me.vincentvibe3.emergencyfood.commands.music.Play
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import kotlin.random.Random

@Bot.Beta
object RequestsTest: SlashCommand() {
    override val name = "test"
    override val command = CommandData(name, "tests requests")

    override suspend fun handle(event: SlashCommandEvent) = coroutineScope {
        event.deferReply().queue()
        launch {
            repeat(100){
                RequestHandler.get("http://127.0.0.1:8000", 1)
            }

        }.join()
        event.hook.editOriginal("done").queue()
    }
}