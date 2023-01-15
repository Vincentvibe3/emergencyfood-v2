package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import org.json.JSONArray

object List: GenericSubCommand(), SubCommand{

    override val name: String = "list"
    override val subCommand: SubcommandData = SubcommandData(name, "List all name roulette possible rolls")

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        val guildId = event.guild?.id
        if (guildId!=null){
            val allRolls = Supabase.select("nameroulette_choices", listOf(
                SupabaseFilter("guild", guildId, SupabaseFilter.Match.EQUALS)
            ))
            val jsonData = JSONArray(allRolls)
            val messageBuilder = MessageCreateBuilder()
            val rolls = arrayListOf<String>()
            val deathRolls = arrayListOf<String>()
            for (index in 0 until jsonData.length()){
                val entryInfo = jsonData.getJSONObject(index)
                if (entryInfo.getBoolean("deathroll")){
                    deathRolls.add(entryInfo.getString("name"))
                } else {
                    rolls.add(entryInfo.getString("name"))
                }
            }
            messageBuilder.addContent("***Entries:***\n")
            messageBuilder.addContent(rolls.joinToString(", ")+"\n")
            messageBuilder.addContent("***Deathrolls:***\n")
            messageBuilder.addContent(deathRolls.joinToString(", "))
            event.hook.editOriginal(MessageEditData.fromCreateData(messageBuilder.build())).queue()
        } else {
            event.hook.editOriginal("Something went wrong").queue()
        }
    }

}