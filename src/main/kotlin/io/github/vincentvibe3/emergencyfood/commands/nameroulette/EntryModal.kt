package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.InteractionModal
import io.github.vincentvibe3.emergencyfood.internals.SelectMenuManager
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteUser
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import java.util.*
import kotlin.collections.ArrayList

class EntryModal(
    val id:String,
    val guild:String,
    override val expires: Boolean=true,
    override var expiry: Long? = DEFAULT_EXPIRY_OFFSET,
    override val uuid: UUID=UUID.randomUUID()
) : InteractionModal() {

    companion object {
        const val NORMAL_LIMIT = Int.MAX_VALUE
        const val DEATH_LIMIT = Int.MAX_VALUE
    }

    override val name: String
        get() = "Name Roulette Submission"

    var addCount = 0
    var addCountDeath = 0

    suspend fun updateModalState(){
        val data = Supabase.select("users",
            listOf(SupabaseFilter("id", "$id:$guild", SupabaseFilter.Match.EQUALS))
        )
        val jsonData = Json.decodeFromString<List<NameRouletteUser>>(data)
        if (jsonData.isNotEmpty()) {
            addCount = jsonData[0].added_choices
            addCountDeath = jsonData[0].added_choices_death
        }
    }

    override val modal: Modal?
        get() {
            val textEntries = ArrayList<ActionRow>()
            if (addCount>= NORMAL_LIMIT&&addCountDeath>= DEATH_LIMIT){
                return null
            }
            for (count in 1..5){
                val input = TextInput.create("entry$count", "Entry $count (optional)", TextInputStyle.SHORT)
                    .setPlaceholder("Enter the name of the series")
                    .setMinLength(1)
                    .setMaxLength(1000)
                    .setRequired(false)
                if (count==1){
                    input.setLabel("Entry $count").isRequired = true
                }
                textEntries.add(ActionRow.of(input.build()))
            }
            return Modal.create(uuid.toString(), name)
                .addComponents(textEntries)
                .build()
        }

    override suspend fun handle(event: ModalInteractionEvent) {
        var addCount = 0
        val selectMenus = ArrayList<ActionRow>()
        event.values.forEach {
            addCount++
            if (it.asString.isNotBlank()){
                val menuHandler = TypeSelectionMenu(it.asString, UUID.randomUUID())
                val menu = menuHandler.menu
                SelectMenuManager.registerLocal(menuHandler)
                selectMenus.add(ActionRow.of(menu))
            }
        }
        event.reply("Select the type of each added entry (The dropdowns will be active for 15 minutes)")
            .setEphemeral(true)
            .addComponents(selectMenus)
            .queue()
    }

}
