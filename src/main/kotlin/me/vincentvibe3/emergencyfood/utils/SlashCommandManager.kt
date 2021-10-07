package me.vincentvibe3.emergencyfood.utils

import me.vincentvibe3.emergencyfood.commands.music.*
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.core.Channel
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import kotlin.reflect.full.memberProperties

object SlashCommandManager {

    //registered commands
    private val commandsList = HashMap<String, SlashCommand>()

    init {
        try{
            //set commands to add here
            registerLocal(Play)
            registerLocal(Pause)
            registerLocal(Loop)
            registerLocal(Skip)
            registerLocal(Disconnect)
            registerLocal(Clear)
            registerLocal(Queue)
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

    fun registerGuildRemote(channel: Channel){
        println("Starting guild command registration")

        if (channel!=Channel.STABLE){
            commandsList.forEach{
                val command = it.value
                val commandData = command::class.memberProperties
                    .first{item -> item.name == "command"}
                    .getter.call(command) as CommandData

                Bot.getClientInstance().guilds.forEach { guild ->
                    guild.upsertCommand(commandData).queue(
                        { println("Added ${command.name} to ${guild.name}") },
                        { println("Failed to add ${command.name} to ${guild.name}") }
                    )
                }
            }
        }
        Bot.getClientInstance().guilds.forEach { guild ->
            guild.retrieveCommands().queue ({ commandList ->
                commandList.filter { !commandsList.containsKey(it.name) }.forEach { command ->
                    try {
                        command.delete().queue(
                            { println("Deleted ${command.name} from ${guild.name}") },
                            { println("Failed to delete ${command.name} from ${guild.name}") }
                        )
                    } catch (e: IllegalAccessException) {
                        println("Failed to delete ${command.name} ${guild.name}")
                    }
                }
            }, {
                println("Failed to fetch commands for ${guild.name}")
            })
        }
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
                    .getter.call(command) as CommandData
            } else {
                command::class.memberProperties
                    .first { item -> item.name == "command" }
                    .getter.call(command) as CommandData
            }
            Bot.getClientInstance()
                .upsertCommand(commandData)
                .queue(
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