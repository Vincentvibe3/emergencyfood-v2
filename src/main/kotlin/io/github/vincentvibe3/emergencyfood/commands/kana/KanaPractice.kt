package io.github.vincentvibe3.emergencyfood.commands.kana

import io.github.vincentvibe3.emergencyfood.internals.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.ActionRow
import kotlin.random.Random

object KanaPractice : GenericCommand(), SlashCommand, MessageCommand {

    private val kanas = listOf(
        hashMapOf(
            "hiragana" to "あ い う え お".split(" "),
            "katakana" to "ア イ ウ エ オ".split(" "),
            "answers" to "a i u e o".split(" ")
        ),
        hashMapOf(
            "hiragana" to "か き く け こ".split(" "),
            "katakana" to "カ キ ク ケ コ".split(" "),
            "answers" to "ka ki ku ke ko".split(" ")
        ),
        hashMapOf(
            "hiragana" to "さ し す せ そ".split(" "),
            "katakana" to "サ シ ス セ ソ".split(" "),
            "answers" to "sa shi su se so".split(" ")
        ),
        hashMapOf(
            "hiragana" to "た ち つ て と".split(" "),
            "katakana" to "タ チ ツ テ ト".split(" "),
            "answers" to "ta chi tsu te to".split(" ")
        ),
        hashMapOf(
            "hiragana" to "な に ぬ ね の".split(" "),
            "katakana" to "ナ ニ ヌ ネ ノ".split(" "),
            "answers" to "na ni nu ne no".split(" ")
        ),
        hashMapOf(
            "hiragana" to "は ひ ふ へ ほ".split(" "),
            "katakana" to "ハ ヒ フ ヘ ホ".split(" "),
            "answers" to "ha hi fu he ho".split(" ")
        ),
        hashMapOf(
            "hiragana" to "ま み む め も".split(" "),
            "katakana" to "マ ミ ム メ モ".split(" "),
            "answers" to "ma mi mu me mo".split(" ")
        ),
        hashMapOf(
            "hiragana" to "や ゆ よ".split(" "),
            "katakana" to "ヤ ユ ヨ".split(" "),
            "answers" to "ya yu yo".split(" ")
        ),
        hashMapOf(
            "hiragana" to "ら り る れ ろ".split(" "),
            "katakana" to "ラ リ ル レ ロ".split(" "),
            "answers" to "ra ri ru re ro".split(" ")
        ),
        hashMapOf(
            "hiragana" to "が ぎ ぐ げ ご".split(" "),
            "katakana" to "ガ ギ グ ゲ ゴ".split(" "),
            "answers" to "ga gi gu ge go".split(" ")
        ),
        hashMapOf(
            "hiragana" to "ざ じ ず ぜ ぞ".split(" "),
            "katakana" to "ザ ジ ズ ゼ ゾ".split(" "),
            "answers" to "za ji zu ze zo".split(" ")
        ),
        hashMapOf(
            "hiragana" to "だ ぢ づ で ど".split(" "),
            "katakana" to "ダ ヂ ヅ デ ド".split(" "),
            "answers" to "da ji zu de do".split(" ")
        ),
        hashMapOf(
            "hiragana" to "ば び ぶ べ ぼ".split(" "),
            "katakana" to "バ ビ ブ ベ ボ".split(" "),
            "answers" to "ba bi bu be bo".split(" ")
        ),
        hashMapOf(
            "hiragana" to "ぱ ぴ ぷ ぺ ぽ".split(" "),
            "katakana" to "パ ピ プ ペ ポ".split(" "),
            "answers" to "pa pi pu pe po".split(" ")
        ),
        hashMapOf("hiragana" to "わ を".split(" "), "katakana" to "ワ ヲ".split(" "), "answers" to "wa wo".split(" ")),
        hashMapOf("hiragana" to "ん".split(" "), "katakana" to "ン".split(" "), "answers" to "n".split(" "))
    )

    override val name = "kana"

    override val command = Commands.slash(name, "practice your kana")
        .addOption(OptionType.STRING, "type", "hiragana, katakana or random (or h,k,r)", false)

    internal fun getQuestion(type: String): Pair<String, String>? {
        var mode = if (type.lowercase() == "r"){
            "random"
        } else if (type.lowercase() == "h") {
            "hiragana"
        } else if (type.lowercase() == "k"){
            "katakana"
        } else {
            type.lowercase()
        }
        val yDim = Random.nextInt(0, 11)
        if (mode == "random") {
            val select = Random.nextInt(0, 2)
            mode = if (select == 0) {
                "hiragana"
            } else {
                "katakana"
            }
        }
        val kanaCat = kanas[yDim][mode]
        val answers = kanas[yDim]["answers"]
        if (kanaCat != null && answers != null) {
            val xDim = Random.nextInt(0, kanaCat.size - 1)
            val answer = answers[xDim]
            val kana = kanaCat[xDim]
            return Pair(kana, answer)
        }
        return null
    }

    internal fun setResponse(user: String, channel: String, answer: String) {
        val response = KanaMessageResponse(user, channel, answer)
        MessageResponseManager.add(response)
    }

    override suspend fun handle(event: MessageReceivedEvent) {
        val type = event.getOptions().firstOrNull()
        if (type != null) {
            val kanaAndAns = getQuestion(type)
            val kana = kanaAndAns?.first
            val ans = kanaAndAns?.second
            if (ans != null) {
                setResponse(event.author.id, event.channel.id, ans)
                event.channel.sendMessage("Which kana is this? $kana").queue()
            } else {
                event.channel.sendMessage("Please pass a valid type").queue()
            }
        } else {
            event.channel.sendMessage("Please pass a type(hiragana, katakana or random)").queue()
        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val type = event.getOption("type")?.asString
        if (type != null) {
            val kanaAndAns = getQuestion(type)
            if (kanaAndAns != null) {
                val kana = kanaAndAns.first
                val ans = kanaAndAns.second
                setResponse(event.user.id, event.channel.id, ans)
                event.reply("Which kana is this? $kana").queue()
            } else {
                val kanaSelectionMenu = KanaModeSelectionMenu()
                SelectMenuManager.registerLocal(kanaSelectionMenu)
                event.reply("Select the kana type you want")
                    .setEphemeral(true)
                    .addComponents(listOf(ActionRow.of(kanaSelectionMenu.menu)))
                    .queue()
            }
        } else {
            val kanaSelectionMenu = KanaModeSelectionMenu()
            SelectMenuManager.registerLocal(kanaSelectionMenu)
            event.reply("Select the kana type you want")
                .setEphemeral(true)
                .addComponents(listOf(ActionRow.of(kanaSelectionMenu.menu)))
                .queue()
        }
    }


}