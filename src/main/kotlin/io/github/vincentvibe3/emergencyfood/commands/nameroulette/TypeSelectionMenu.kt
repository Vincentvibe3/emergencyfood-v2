package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.InteractionSelectMenu
import io.github.vincentvibe3.emergencyfood.internals.SelectMenuManager
import io.github.vincentvibe3.emergencyfood.utils.nameroulette.NamerouletteEventLoop
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.json.JSONArray
import java.util.*

class TypeSelectionMenu(override val name: String) :InteractionSelectMenu() {

    override val menu: StringSelectMenu.Builder = StringSelectMenu.create(name)

    override val expires: Boolean = true

    private suspend fun exists(guildId:String, name:String): Boolean {
        val result = Supabase.select("nameroulette_choices", listOf(
            SupabaseFilter("guild", guildId, SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = JSONArray(result)
        for (index in 0 until jsonData.length()){
            val entry = jsonData.getJSONObject(index)
            if (entry.getString("name").lowercase() == name.lowercase()){
                return true
            }
        }
        return false
    }

    override suspend fun handle(event: StringSelectInteraction) {
        val guild = event.guild
        if (guild!=null){
            val rawData = Supabase.select("users", listOf(
                SupabaseFilter("id", "${event.user.id}:${guild.id}", SupabaseFilter.Match.EQUALS)
            ))
            val addCount = JSONArray(rawData).getJSONObject(0).getInt("added_choices")
            val addCountDeath = JSONArray(rawData).getJSONObject(0).getInt("added_choices_death")
            if ((addCount<3&&event.selectedOptions[0].value == "normal")||addCountDeath<2&&event.selectedOptions[0].value == "deathroll") {
                if (exists(guild.id, event.componentId)){
                    event.reply("This entry is already in Name Roulette").queue()
                } else {
                    Supabase.insert(
                        "nameroulette_choices", hashMapOf(
                            "name" to event.componentId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            "guild" to event.guild!!.id,
                            "added_by" to event.user.id,
                            "deathroll" to (event.selectedOptions[0].value != "normal")
                        )
                    )
                    if (event.selectedOptions[0].value == "deathroll"){
                        Supabase.update(
                            "users", hashMapOf(
                                "added_choices_death" to addCountDeath + 1
                            ), listOf(SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS))
                        )
                        event.reply("${event.user.asMention} added ${event.componentId} as a deathroll").queue()
                    } else {
                        Supabase.update(
                            "users", hashMapOf(
                                "added_choices" to addCount + 1
                            ), listOf(SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS))
                        )
                        event.reply("${event.user.asMention} added ${event.componentId}").queue()
                    }
                }
                SelectMenuManager.unregisterLocal(this)
                NamerouletteEventLoop.activeDropdowns.remove(this)
            } else {
                event.reply("You do not have any more entries of this type to add").setEphemeral(true).queue()
            }
        } else {
            event.reply("This must be done in a server").setEphemeral(true).queue()
        }
    }
}