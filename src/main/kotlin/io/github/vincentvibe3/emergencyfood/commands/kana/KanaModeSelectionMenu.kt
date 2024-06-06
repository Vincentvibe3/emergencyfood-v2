package io.github.vincentvibe3.emergencyfood.commands.kana

import io.github.vincentvibe3.emergencyfood.commands.kana.KanaPractice.getQuestion
import io.github.vincentvibe3.emergencyfood.commands.kana.KanaPractice.setResponse
import io.github.vincentvibe3.emergencyfood.internals.InteractionSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class KanaModeSelectionMenu: InteractionSelectMenu() {

    override val name: String = "ModeSelection"
    override val menu: StringSelectMenu = StringSelectMenu.create(uuid.toString())
        .addOption("Hiragana", "hiragana", "Get a random hiragana")
        .addOption("Katakana", "katakana", "Get a random katakana")
        .addOption("Random", "random", "Get any random kana")
        .build()

    override suspend fun handle(event: StringSelectInteraction) {
        val type = event.values.first()
        val kanaAndAns = getQuestion(type)
        if (kanaAndAns != null) {
            val kana = kanaAndAns.first
            val ans = kanaAndAns.second
            setResponse(event.user.id, event.channel.id, ans)
            event.reply("Which kana is this? $kana").queue()
        }
    }
}