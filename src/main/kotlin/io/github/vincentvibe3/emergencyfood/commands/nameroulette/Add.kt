package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.MessageSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object Add: GenericSubCommand(), SubCommand{

    override val name: String = "add"

    override val subCommand: SubcommandData = SubcommandData(name, "add an entry")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val modal = event.member?.let { event.guild?.let { it1 -> EntryModal.getModal(it.id, it1.id) } }
        if (modal!=null){
            event.replyModal(modal).queue()
        } else {
            event.reply("You have no entries left to submit").queue()
        }
    }
}