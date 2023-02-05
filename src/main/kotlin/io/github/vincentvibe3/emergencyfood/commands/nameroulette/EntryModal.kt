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

object EntryModal: InteractionModal() {
    override val name: String
        get() = "nameroulettemodal"

    override val modal: Modal
        get() {
            return Modal.create(name, "Name Roulette Submission")
                .build()
        }

    suspend fun getModal(id:String, guild:String): Modal? {
        val data = Supabase.select("users",
            listOf(SupabaseFilter("id", "$id:$guild", SupabaseFilter.Match.EQUALS))
        )
        val jsonData = Json.decodeFromString<List<NameRouletteUser>>(data)
        return if (jsonData.isNotEmpty()){
            val addCount = jsonData[0].added_choices
            val addCountDeath = jsonData[0].added_choices_death
            val textEntries = ArrayList<ActionRow>()
            if (addCount>=3&&addCountDeath>=2){
                return null
            }
            for (count in 1..(5-addCount-addCountDeath)){
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
            Modal.create(name, "Name Roulette Submission")
                .addActionRows(textEntries)
                .build()
        } else {
            null
        }

    }

    override suspend fun handle(event: ModalInteractionEvent) {
        var addCount = 0
        val selectMenus = ArrayList<ActionRow>()
        event.values.forEach {
            addCount++
            if (it.asString.isNotBlank()){
                val menuHandler = TypeSelectionMenu(it.asString)
                menuHandler.expiry = System.currentTimeMillis()+900000
                val menu = menuHandler.menu
                    .addOption("Set ${it.asString} as normal", "normal", "Set as normal")
                    .addOption("Set ${it.asString} as deathroll", "deathroll", "Set as deathroll")
                    .build()
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
