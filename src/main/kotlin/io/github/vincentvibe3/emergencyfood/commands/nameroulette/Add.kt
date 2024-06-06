package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.ModalManager
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object Add: GenericSubCommand(), SubCommand{

    override val name: String = "add"

    override val subCommand: SubcommandData = SubcommandData(name, "add an entry")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val modalInteraction = event.member?.let { member -> event.guild?.let { guild -> EntryModal(member.id, guild.id) } }
        if (modalInteraction != null) {
            ModalManager.registerLocal(modalInteraction)
        }
        modalInteraction?.updateModalState()
        val modal = modalInteraction?.modal
        if (modal!=null){
            event.replyModal(modal).queue()
        } else {
            event.reply("You have no entries left to submit").queue()
        }
    }
}