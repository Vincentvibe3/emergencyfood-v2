package me.vincentvibe3.emergencyfood.internals

import me.vincentvibe3.emergencyfood.commands.music.*
import me.vincentvibe3.emergencyfood.commands.sauce.Sauce
import me.vincentvibe3.emergencyfood.core.Bot
import me.vincentvibe3.emergencyfood.core.Channel
import me.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

object SlashCommandManager {

    //registered commands
    private val commandsList = HashMap<String, SlashCommand>()

    init {
        try{
            //set commands to add here
            registerLocal(Sauce)
            registerLocal(Play)
            registerLocal(Pause)
            registerLocal(Loop)
            registerLocal(Skip)
            registerLocal(Disconnect)
            registerLocal(Clear)
            registerLocal(Queue)
            registerLocal(NowPlaying)
            registerLocal(Remove)
            registerLocal(Shuffle)
        } catch (e:IllegalArgumentException) {
            Logging.logger.error("Failed to add ${e.stackTrace[5].className.split(".").last()}")
        }

    }

    //get hashmap with commands
    fun getCommands():HashMap<String, SlashCommand> {
        return commandsList
    }

    //add commands to the hashmap
    private fun registerLocal(command: SlashCommand){
        commandsList[command.name] = command
    }

    /*this method is only currently used to accelerate testing and
    * therefore does not support use of actual guild commands*/
    fun registerGuildRemote(channel: Channel){
        Logging.logger.info("Starting guild command registration")

        //register guild commands if not on stable
        if (channel!=Channel.STABLE){
            commandsList.forEach{
                val command = it.value
                val commandData = command::class.memberProperties
                    .first{item -> item.name == "command"}
                    .getter.call(command) as CommandData

                Bot.getClientInstance().guilds.forEach { guild ->
                    guild.upsertCommand(commandData).queue(
                        { Logging.logger.info("Added ${command.name} to ${guild.name}") },
                        { Logging.logger.error("Failed to add ${command.name} to ${guild.name}") }
                    )
                }
            }
        }
        //remove old guild commands
        Bot.getClientInstance().guilds.forEach { guild ->
            guild.retrieveCommands().queue ({ commandList ->
                commandList.filter { !commandsList.containsKey(it.name) }.forEach { command ->
                    try {
                        command.delete().queue(
                            { Logging.logger.info("Deleted ${command.name} from ${guild.name}") },
                            { Logging.logger.error("Failed to delete ${command.name} from ${guild.name}") }
                        )
                    } catch (e: IllegalAccessException) {
                        Logging.logger.error("Failed to delete ${command.name} ${guild.name}")
                    }
                }
            }, {
                Logging.logger.error("Failed to fetch commands for ${guild.name} ")
            })
        }
    }

    //register commands on discord
    fun registerRemote(channel: Channel){
        Logging.logger.info("Starting command registration")
        //upsert new commands
        commandsList.forEach {
            val command = it.value
            val isStable =
                command::class.annotations.none { annotation -> annotation.annotationClass == Bot.Beta::class.createInstance().annotationClass }
            val commandData = command::class.memberProperties
                .first { item -> item.name == "command" }
                .getter.call(command) as CommandData

            val upsert = if (channel == Channel.STABLE && isStable) {
                true
            } else channel != Channel.STABLE && isStable
            //upsert
            if (upsert){
                Bot.getClientInstance()
                    .upsertCommand(commandData)
                    .queue(
                        { Logging.logger.info("Added ${command.name}") },
                        { Logging.logger.error("Failed to add ${command.name}") })
            }

        }

        //old command deletion
        val toDelete = ArrayList<Command>()
        Bot.getClientInstance().retrieveCommands().queue{ remoteCommandList ->
            //delete remote commands that are locally marked as beta
            if (channel==Channel.STABLE){
                remoteCommandList.filter { commandsList.containsKey(it.name) }
                    .filter {
                        commandsList.getValue(it.name)::class.annotations
                            .firstOrNull { annotation -> annotation.annotationClass == Bot.Beta::class.createInstance().annotationClass } != null }
                    .forEach {
                        toDelete.add(it)
                    }
            }

            //delete commands that exist remotely but not locally
            remoteCommandList.filter { !commandsList.containsKey(it.name) }.forEach {
                toDelete.add(it)
            }

            //delete commands
            toDelete.forEach{ command ->
                try {
                    command.delete().queue(
                        { Logging.logger.info("Deleted ${command.name}") },
                        { Logging.logger.error("Failed to delete ${command.name}") }
                    )
                } catch (e:IllegalAccessException){
                    Logging.logger.error("Failed to delete ${command.name}")
                }
            }
        }
    }

}