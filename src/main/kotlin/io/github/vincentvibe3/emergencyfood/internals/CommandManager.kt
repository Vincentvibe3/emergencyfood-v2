package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.commands.admin.Admin
import io.github.vincentvibe3.emergencyfood.commands.anime.Anime
import io.github.vincentvibe3.emergencyfood.commands.kana.KanaPractice
import io.github.vincentvibe3.emergencyfood.commands.misc.Roll
import io.github.vincentvibe3.emergencyfood.commands.music.*
import io.github.vincentvibe3.emergencyfood.commands.sauce.Sauce
import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.ConfigLoader.Channel
import io.github.vincentvibe3.emergencyfood.utils.Logging
import net.dv8tion.jda.api.interactions.commands.Command
<<<<<<< HEAD
=======
import java.util.concurrent.ExecutionException
import kotlin.reflect.full.createInstance
>>>>>>> 3888cf6 (Improved command update flow)

object CommandManager {

    //registered commands
    private val slashCommandsList = HashMap<String, SlashCommand>()
    private val messageCommandsList = HashMap<String, MessageCommand>()

    init {
        try {
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
            registerLocal(Roll)
            registerLocal(Admin)
        } catch (e: IllegalArgumentException) {
            Logging.logger.error("Failed to add ${e.stackTrace[5].className.split(".").last()}")
        }

    }

    //get hashmap with commands
    fun getSlashCommands(): HashMap<String, SlashCommand> {
        return slashCommandsList
    }

    fun getMessageCommands(): HashMap<String, MessageCommand> {
        return messageCommandsList
    }

    //add commands to the hashmap
    private fun registerLocal(command: GenericCommand) {
        if (!Config.exclusions.contains(command.name)) {
            if (command is MessageCommand) {
                messageCommandsList[command.name] = command
            }
            if (command is SlashCommand) {
                slashCommandsList[command.name] = command
            }
        }
    }

    /* this method is only currently used to accelerate testing and
    * therefore does not support use of actual guild commands*/
    fun registerGuildRemote(channel: Channel) {
        Logging.logger.info("Starting guild command registration")
        //register guild commands if not on stable
        if (channel != Channel.STABLE) {
<<<<<<< HEAD
            slashCommandsList.forEach {
                val command = it.value as GenericCommand
                if (command is SlashCommand){
                    val commandData = command.command
                    Bot.getClientInstance().guilds.forEach { guild ->
                        if (guild.id==Config.testServer){
                            guild.upsertCommand(commandData).queue(
                                { Logging.logger.info("Added ${command.name} to ${guild.name}") },
                                { Logging.logger.error("Failed to add ${command.name} to ${guild.name}") }
                            )
                        }
=======
            val toDelete = ArrayList<String>()
            val client = Bot.getClientInstance()
            val guild = client.guilds.first { it.id == Config.testServer }
            val updates = guild
                .updateCommands()
            var remoteCommands: MutableList<Command> = ArrayList()
            try {
                remoteCommands = guild.retrieveCommands().complete()
                val names = remoteCommands.map { remote -> remote.name }
                val localNames = slashCommandsList.map {local -> (local.value as GenericCommand).name}
                names.filter { remote -> localNames.contains(remote).not() }
                    .forEach { name ->
                        println(name)
                        toDelete.add(name)
>>>>>>> 3888cf6 (Improved command update flow)
                    }
            } catch (e:ExecutionException){
                Logging.logger.warn("Canceled guild commands fetch")
            } catch (e:RuntimeException){
                Logging.logger.warn("Failed to fetch remote guild commands. Commands will be updated without deletion message")
            }


            slashCommandsList.forEach {
                val command = it.value
                val commandData = command.command
                updates.addCommands(commandData)
            }
            updates.queue ({
                var updateCount = 0
                it.forEach { addedCommand ->
                    if (remoteCommands.contains(addedCommand).not()) {
                        Logging.logger.info("Added ${addedCommand.name} to ${guild.name}")
                    } else {
                        updateCount++
                    }
                }
                Logging.logger.info("Updated $updateCount commands in ${guild.name}")
            }, {
                Logging.logger.error("Failed to update commands in ${guild.name}")
                it.printStackTrace()
            })
            toDelete.forEach {
                Logging.logger.warn("$it will be deleted in ${guild.name}")
            }
        }
    }

    //register commands on discord
    fun registerRemote(channel: Channel) {
        Logging.logger.info("Starting command registration")
        val toDelete = ArrayList<String>()
        val client = Bot.getClientInstance()
        val updates = client
            .updateCommands()
        var remoteCommands: MutableList<Command> = ArrayList()
        try {
            remoteCommands = client.retrieveCommands().complete()
            val names = remoteCommands.map { remote -> remote.name }
            val localNames = slashCommandsList.map {local -> (local.value as GenericCommand).name}
            names.filter { remote -> localNames.contains(remote).not() }
                .forEach { name ->
                    println(name)
                    toDelete.add(name)
                }
            remoteCommands.first()
        } catch (e:ExecutionException){
            Logging.logger.warn("Canceled commands fetch")
        } catch (e:RuntimeException){
            Logging.logger.warn("Failed to fetch remote commands. Commands will be updated without deletion message")
        }
        //upsert new commands
        slashCommandsList.forEach {
<<<<<<< HEAD
            val command = it.value as GenericCommand
            val isStable = !command.beta
            if (command is SlashCommand){
                val commandData = command.command
                val upsert = if (channel == Channel.STABLE && isStable) {
                    true
                } else channel != Channel.STABLE && isStable

                //upsert
                if (upsert){
                    Bot.getClientInstance()
                        .upsertCommand(commandData)
                        .queue(
                            { Logging.logger.info("Added ${command.name}") },
                            { Logging.logger.error("Failed to add ${command.name}") }
                        )
                }
            }

        }

        //old command deletion
        val toDelete = HashMap<String, ArrayList<Command>>()
        toDelete["guild"] = ArrayList()
        toDelete["global"] = ArrayList()
        if (channel == Channel.STABLE) {
            Bot.getClientInstance().guilds.forEach { guild ->
                guild.retrieveCommands().queue { guildCommands ->
                    guildCommands.forEach { toDelete["guild"]?.add(it) }
=======
            val command = it.value
            val name = (command as GenericCommand).name
            val isStable =
                command::class.annotations.none { annotation -> annotation.annotationClass == Bot.Beta::class.createInstance().annotationClass }
            val commandData = command.command
            if (channel == Channel.STABLE && !isStable) {
                if (toDelete.contains(name).not()){
                    toDelete.add(name)
>>>>>>> 3888cf6 (Improved command update flow)
                }
            } else {
                updates.addCommands(commandData)
            }
        }
<<<<<<< HEAD

        Bot.getClientInstance().retrieveCommands().queue { remoteCommandList ->
            //delete remote commands that are locally marked as beta
            if (channel == Channel.STABLE) {
                remoteCommandList.filter { slashCommandsList.containsKey(it.name) }
                    .filter {
                        (slashCommandsList.getValue(it.name) as GenericCommand).beta
                    }.forEach {
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
=======
        toDelete.forEach {
            Logging.logger.warn("$it will be deleted")
        }
        updates.queue({
            var updateCount = 0
            it.forEach { addedCommand ->
                if (remoteCommands.contains(addedCommand).not()) {
                    Logging.logger.info("Added ${addedCommand.name}")
                } else {
                    updateCount++
>>>>>>> 3888cf6 (Improved command update flow)
                }
            }
            Logging.logger.info("Updated $updateCount commands")
        }, {
            Logging.logger.error("Failed to update commands")
            it.printStackTrace()
        })
    }

}