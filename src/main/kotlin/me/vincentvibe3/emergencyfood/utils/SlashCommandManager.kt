package me.vincentvibe3.emergencyfood.utils

import me.vincentvibe3.emergencyfood.commands.music.Play
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.core.Channel
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import kotlin.reflect.full.memberProperties

object SlashCommandManager {

    //registered commands
    private val commandsList = HashMap<String, SlashCommand>()

    init {
        try{
            //set commands to add here
            registerLocal(Play())
        } catch (e:IllegalArgumentException) {
            println("Failed to add ${e.stackTrace[5].className.split(".").last()}")
        }

    }

    //get hashmap with commands
    fun getCommands():HashMap<String, SlashCommand> {
        return commandsList
    }

    //add commands to the hashmap
    private fun registerLocal(command:SlashCommand){
        commandsList[command.name] = command
    }

    //register commands on discord
    fun registerRemote(channel: Channel){
        println("Starting command registration")

        //upsert new commands
        commandsList.forEach{
            val command = it.value
            val commandData = if (channel == Channel.STABLE){
                command::class.memberProperties
                    .filter { item -> item.annotations.find { property -> property.annotationClass == Bot.Beta::annotationClass } == null }
                    .first { item -> item.name == "command" }
                    .getter.call(command) as CommandCreateAction
            } else {
                command::class.memberProperties
                    .first { item -> item.name == "command" }
                    .getter.call(command) as CommandCreateAction
            }
            commandData.queue(
                { println("Added ${command.name}") },
                { println("Failed to add ${command.name}") }
            )
        }

        //delete old commands
        Bot.getClientInstance().retrieveCommands().queue{ commandList ->
            commandList.filter { !commandsList.containsKey(it.name) }.forEach { command ->
                try {
                    command.delete().queue(
                        { println("Deleted ${command.name}") },
                        { println("Failed to delete ${command.name}") }
                    )
                } catch (e:IllegalAccessException){
                    println("Failed to delete ${command.name}")
                }
            }
        }
    }

}