package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.commands.anime.Anime
import io.github.vincentvibe3.emergencyfood.commands.kana.KanaPractice
import io.github.vincentvibe3.emergencyfood.commands.music.*
import io.github.vincentvibe3.emergencyfood.commands.sauce.Sauce
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.ConfigLoader.Channel
import io.github.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

object CommandManager {

    //registered commands
    private val slashCommandsList = HashMap<String, SlashCommand>()
    private val messageCommandsList = HashMap<String, MessageCommand>()

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
            registerLocal(Anime)
            registerLocal(KanaPractice)
        } catch (e:IllegalArgumentException) {
            Logging.logger.error("Failed to add ${e.stackTrace[5].className.split(".").last()}")
        }

    }

    //get hashmap with commands
    fun getSlashCommands():HashMap<String, SlashCommand> {
        return slashCommandsList
    }

    fun getMessageCommands():HashMap<String, MessageCommand> {
        return messageCommandsList
    }

    //add commands to the hashmap
    private fun registerLocal(command: GenericCommand){
        if (!Config.exclusions.contains(command.name)){
            if (command is MessageCommand){
                messageCommandsList[command.name] = command
            }
            if (command is SlashCommand){
                slashCommandsList[command.name] = command
            }
        }
    }

    /*this method is only currently used to accelerate testing and
    * therefore does not support use of actual guild commands*/
    fun registerGuildRemote(channel: Channel){
        Logging.logger.info("Starting guild command registration")

        //register guild commands if not on stable
        if (channel!=Channel.STABLE){
            slashCommandsList.forEach{
                val command = it.value as GenericCommand
                val commandData = command::class.memberProperties
                    .first{item -> item.name == "command"}
                    .getter.call(command) as CommandData

                Bot.getClientInstance().guilds.forEach { guild ->
                    if (guild.id==Config.testServer){
                        guild.upsertCommand(commandData).queue(
                            { Logging.logger.info("Added ${command.name} to ${guild.name}") },
                            { Logging.logger.error("Failed to add ${command.name} to ${guild.name}") }
                        )
                    }
                }
            }
        }
        //remove old guild commands
        Bot.getClientInstance().guilds.forEach { guild ->
            guild.retrieveCommands().queue ({ commandList ->
                commandList.filter { !slashCommandsList.containsKey(it.name) }.forEach { command ->
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
        slashCommandsList.forEach {
            val command = it.value as GenericCommand
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
        val toDelete = HashMap<String, ArrayList<Command>>()
        toDelete["guild"] = ArrayList()
        toDelete["global"] = ArrayList()
        if (channel==Channel.STABLE){
            Bot.getClientInstance().guilds.forEach{ guild ->
                guild.retrieveCommands().queue{ guildCommands ->
                    guildCommands.forEach{ toDelete["guild"]?.add(it) }
                }
            }
        }

        Bot.getClientInstance().retrieveCommands().queue{ remoteCommandList ->
            //delete remote commands that are locally marked as beta
            if (channel==Channel.STABLE){
                remoteCommandList.filter { slashCommandsList.containsKey(it.name) }
                    .filter {
                        slashCommandsList.getValue(it.name)::class.annotations
                            .firstOrNull { annotation -> annotation.annotationClass == Bot.Beta::class.createInstance().annotationClass } != null }
                    .forEach {
                        toDelete["global"]?.add(it)
                    }
            }

            //delete commands that exist remotely but not locally
            remoteCommandList.filter { !slashCommandsList.containsKey(it.name) }.forEach {
                toDelete["global"]?.add(it)
            }

            //delete commands
            toDelete.forEach { type ->
                type.value.forEach { command ->
                    try {
                        command.delete().queue(
                            { Logging.logger.info("Deleted ${command.name} ${type.key}") },
                            { Logging.logger.error("Failed to delete ${command.name} ${type.key}") }
                        )
                    } catch (e: IllegalAccessException) {
                        Logging.logger.error("Failed to delete ${command.name} ${type.key}")
                    }
                }
            }
        }
    }

}