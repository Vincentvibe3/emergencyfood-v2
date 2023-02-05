package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.InteractionSelectMenu
import io.github.vincentvibe3.emergencyfood.internals.SelectMenuManager
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteRoll
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteUser
import io.github.vincentvibe3.emergencyfood.utils.nameroulette.NamerouletteEventLoop
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import java.util.*

class TypeSelectionMenu(override val name: String) :InteractionSelectMenu() {

    override val menu: StringSelectMenu.Builder = StringSelectMenu.create(name)

    override val expires: Boolean = true

    private suspend fun exists(guildId:String, name:String): Boolean {
        val result = Supabase.select("nameroulette_choices", listOf(
            SupabaseFilter("guild", guildId, SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = Json.decodeFromString<List<NameRouletteRoll>>(result)
        for (element in jsonData){
            if (element.name.lowercase() == name.lowercase()){
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
            val jsonData = Json.decodeFromString<List<NameRouletteUser>>(rawData)
            val addCount = jsonData[0].added_choices
            val addCountDeath = jsonData[0].added_choices_death
            if ((addCount<3&&event.selectedOptions[0].value == "normal")||addCountDeath<2&&event.selectedOptions[0].value == "deathroll") {
                if (exists(guild.id, event.componentId)){
                    event.reply("This entry is already in Name Roulette").queue()
                } else {
                    Supabase.insert(
                        "nameroulette_choices", Json.encodeToString(
                            NameRouletteRoll.serializer(),
                            NameRouletteRoll(
                                event.componentId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                event.guild!!.id,
                                event.user.id,
                                event.selectedOptions[0].value != "normal"
                            )
                        )
                    )
                    if (event.selectedOptions[0].value == "deathroll"){
                        Supabase.update(
                            "users", Json.encodeToString(JsonObject.serializer(), JsonObject(mapOf(
                                "added_choices_death" to JsonPrimitive(addCountDeath + 1)
                            ))), listOf(SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS))
                        )
                        event.reply("${event.user.asMention} added ${event.componentId} as a deathroll").queue()
                    } else {
                        Supabase.update(
                            "users", Json.encodeToString(JsonObject.serializer(), JsonObject(mapOf(
                                "added_choices" to JsonPrimitive(addCount + 1)
                            ))), listOf(SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS))
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