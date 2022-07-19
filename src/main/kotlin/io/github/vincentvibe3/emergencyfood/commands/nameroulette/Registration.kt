package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object Registration:GenericSubCommand(), SubCommand {
    override val name: String = "registration"

    override val subCommand: SubcommandData = SubcommandData(name, "register or unregister")

    private suspend fun checkIfUserExists(id:String, guildId:String): Boolean {
        val result = Supabase.select("users", listOf(
            SupabaseFilter("id", "$id:$guildId", SupabaseFilter.Match.EQUALS)
        ))
        return result != "[]"
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guild = event.guild
        if (guild!=null){
            val hasNameRoulette = Setup.checkIfExists(guild.id)
            if (hasNameRoulette){
                if (checkIfUserExists(event.user.id, guild.id)){
                    Supabase.delete("users", listOf(
                        SupabaseFilter("id", "${event.user.id}:${guild.id}", SupabaseFilter.Match.EQUALS)
                    ))
                    event.reply("Unregistered from Name Roulette").setEphemeral(true).queue()
                } else {
                    val result = Supabase.insert("users", hashMapOf(
                        "id" to "${event.user.id}:${guild.id}",
                        "guild" to guild.id,
                        "roll_count" to 0,
                        "deathroll" to false,
                        "roll_names" to "[]",
                        "added_choices" to 0,
                        "added_choices_death" to 0
                    ))
                    println(result)
                    if (result.contains("message")){
                        event.reply("An error occurred").setEphemeral(true).queue()
                    } else {
                        event.reply("You are now registered").setEphemeral(true).queue()
                    }
                }

            } else {
                event.reply("This server does not have Name Roulette").setEphemeral(true).queue()
            }
        } else {
            event.reply("this must be done from a server").setEphemeral(true).queue()
        }
    }
}