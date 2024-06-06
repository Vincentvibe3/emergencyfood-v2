package io.github.vincentvibe3.emergencyfood.internals

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.utils.audio.common.PlayerManager
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

object UniversalListener:ListenerAdapter() {
    private val coroutineScope = CoroutineScope(Dispatchers.Default+SupervisorJob())

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val client = Bot.getClientInstance()
        val name = client.guilds.first { it.id == event.guild.id }.selfMember.effectiveName
        val response = MessageResponseManager.get(event.author.id, event.channel.id)
        if (response != null) {
            MessageResponseManager.remove(response)
            response.handle(event)
        }
        if (checkMessageForCommand(event)) {
            Logging.logger.debug("MessageCommand received")
            val message = event.message.contentDisplay.replace("@$name", "").replace(Config.prefix, "").trim()
            val commandName = message.split(" ")[0]
            coroutineScope.launch {
                retrieveMessageCommand(commandName)?.handle(event)
            }
        }
    }

    private fun checkMessageForCommand(event: MessageReceivedEvent): Boolean {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val name = client.guilds.first { it.id == event.guild.id }.selfMember.effectiveName
        val message = event.message
        return if (message.contentDisplay.startsWith(Config.prefix)) {
            true
        } else if (message.mentions.members.contains(selfMember)) {
            message.contentDisplay.startsWith("@$name")
        } else {
            false
        }
    }

    private fun retrieveMessageCommand(name: String): MessageCommand? {
        return CommandManager.getMessageCommands()[name]
    }

    //find the required command and run its handler function
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        Logging.logger.debug("SlashCommand ${event.name} called")
        coroutineScope.launch {
            retrieveSlashCommand(event.name)?.handle(event)
        }
    }

    //find a register command from its name
    private fun retrieveSlashCommand(name: String): SlashCommand? {
        return CommandManager.getSlashCommands()[name]
    }

    //respond to a button being clicked
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        Logging.logger.debug("Button ${event.componentId} pressed")
        coroutineScope.launch {
            retrieveButton(event.componentId)?.handle(event)
        }
    }

    //get a button
    private fun retrieveButton(name: String): InteractionButton? {
        return ButtonManager.getButtons()[name]
    }

    //respond to a button being clicked
    override fun onModalInteraction(event: ModalInteractionEvent) {
        Logging.logger.debug("Modal ${event.modalId} triggered")
        coroutineScope.launch {
            val modal = retrieveModal(event.modalId)
            if (modal == null){
                event.reply("Modal has expired").setEphemeral(true).queue()
            } else {
                modal.handle(event)
            }
        }
    }

    //get a modal
    private fun retrieveModal(name: String): InteractionModal? {
        return ModalManager.getModals()[UUID.fromString(name)]
    }

    // display info when bot is logged in
    override fun onReady(event: ReadyEvent) {
        Logging.logger.info("Logged in as ${event.jda.selfUser.name}#${event.jda.selfUser.discriminator}")
        Logging.logger.info("Active in ${event.jda.guilds.size} guilds")

    }

    //check if the bot is alone
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val client = Bot.getClientInstance()
        val selfId = client.selfUser.id
        val selfMember = event.guild.getMemberById(selfId)
        val channelLeft = event.channelLeft
        val channelJoin = event.channelJoined
        val guild = event.guild
        val guildId = guild.id

        if (channelLeft!=null&&channelLeft.members.contains(selfMember)){
            if (channelLeft.members.none { !it.user.isBot }){
                PlayerManager.setForCleanup(guildId)
                Logging.logger.debug("Player set for cleanup")
            }
        } else if (channelJoin!=null&&channelJoin.members.contains(selfMember)){
            if (!event.member.user.isBot){
                Logging.logger.debug("Cancelling player cleanup")
                PlayerManager.unsetForCleanup(guildId)
            }
        }

    }

    override fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {
        if (event.member.id == Bot.getClientInstance().selfUser.id) {
            if (!event.isGuildDeafened) {
                event.member.deafen(true).queue()
                val guild = event.guild.id
                val player = PlayerManager.getPlayer(guild)
                val messageChannel = player.getAnnouncementChannel()
                val client = Bot.getClientInstance()
                client.getTextChannelById(messageChannel)?.sendMessage("Please do not server undeafen the bot")?.queue()
            }
        }
    }

    //get a menu
    private fun retrieveMenu(name: String): InteractionSelectMenu? {
        return SelectMenuManager.getMenus()[UUID.fromString(name)]
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        coroutineScope.launch {
            val menu = retrieveMenu(event.componentId)
            if (menu==null){
                event.reply("This dropdown has expired").setEphemeral(true).queue()
            } else {
                menu.handle(event)
            }
        }
    }
}