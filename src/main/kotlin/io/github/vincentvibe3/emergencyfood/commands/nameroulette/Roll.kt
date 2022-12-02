package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random
import kotlin.random.nextInt

object Roll: GenericSubCommand(), SubCommand {

    override val name: String = "roll"

    override val subCommand: SubcommandData = SubcommandData(name, "rolls")

    private suspend fun getRoll(guildId: String): String? {
        val rawRollData = Supabase.select(
            "nameroulette_choices", listOf(
                SupabaseFilter("deathroll", "false", SupabaseFilter.Match.EQUALS),
                SupabaseFilter("guild", guildId, SupabaseFilter.Match.EQUALS)
            )
        )
        val jsonRollData = JSONArray(rawRollData)
        return if (jsonRollData.isEmpty) {
            null
        } else {
            val index = Random.nextInt(0 until jsonRollData.length())
            jsonRollData.getJSONObject(index).getString("name")
        }
    }

    private fun getDeath(rollCount:Int): Boolean {
        val chances = listOf(33, 69)
        val randNum = Random.nextInt(1..100)
        return randNum<=chances[rollCount-1]
    }

    private suspend fun buildAndUpdateMessage(guildId:String): Boolean {
        val messageBuilder = MessageCreateBuilder()
        val rawData = Supabase.select("users", listOf(
            SupabaseFilter("guild", guildId, SupabaseFilter.Match.EQUALS)
        ))
        val rawDataGuild = Supabase.select("guilds", listOf(
            SupabaseFilter("id", guildId, SupabaseFilter.Match.EQUALS)
        ))
        val guildsDataJson = JSONArray(rawDataGuild)
        return if (guildsDataJson.isEmpty){
           false
        } else {
            val guildDataJson = guildsDataJson.getJSONObject(0)
            val deathroll = guildDataJson.getString("current_deathroll")
            messageBuilder.addContent("***Name roulette results:***")
            messageBuilder.addContent("This week's deathroll is $deathroll")
            val usersData = JSONArray(rawData)
            for (index in 0 until usersData.length()){
                val user = usersData.getJSONObject(index)
                val rolls = user.getJSONArray("roll_names");
                if (user.getBoolean("deathroll")){
                    rolls.put("Deathroll")
                }
                messageBuilder.addContent("<@${user.getString("id").split(":")[0]}>: ${rolls.joinToString(", ")}")
            }
            updateMessage(guildDataJson, messageBuilder.build())
            return true
        }
    }

    private fun updateMessage(guildData: JSONObject, message: MessageCreateData){
        val lastMessageData = guildData.getString("last_message").split(":")
        val originalMessageId = lastMessageData[0]
        val channelId = lastMessageData[1]
        val client = Bot.getClientInstance()
        client.getTextChannelById(channelId)
            ?.retrieveMessageById(originalMessageId)
            ?.queue{
                it.editMessage(MessageEditData.fromCreateData(message)).queue()
            }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val rawUserData = Supabase.select(
            "users", listOf(
                SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS)
            )
        )
        val guild = event.guild
        val jsonUserData = JSONArray(rawUserData)
        if (jsonUserData.isEmpty) {
            event.reply("You are not registered").queue()
        } else if (guild == null) {
            event.reply("This cannot be used outside of a server").queue()
        } else {
            val userData = jsonUserData.getJSONObject(0)
            val rolls = userData.getInt("roll_count")
            if (userData.getBoolean("deathroll")){
                event.reply("You got the deathroll. You may not reroll").setEphemeral(true).queue()
            }
            if (rolls >= 3) {
                event.reply("You do not have any more rolls").queue()
            } else {
                event.deferReply(true).queue()
                val isDeath = getDeath(rolls)
                val rollsData = userData.getJSONArray("roll_names")
                val row:HashMap<String, Any> = hashMapOf(
                    "roll_count" to rolls + 1,
                )
                if (isDeath){
                    row["deathroll"] = true
                } else {
                    var roll = getRoll(guild.id)
                    if (roll == null) {
                        event.reply("No rolls are available").queue()
                    }
                    while (rollsData.contains(roll)){
                       roll = getRoll(guild.id)
                    }
                    rollsData.put(roll)
                }
                row["roll_names"] = rollsData
                Supabase.update(
                    "users",
                    row,
                    listOf(
                        SupabaseFilter(
                            "id",
                            "${event.user.id}:${event.guild!!.id}",
                            SupabaseFilter.Match.EQUALS
                        )
                    )
                )
                val message = buildAndUpdateMessage(guild.id)
                if (message){
                    if (isDeath){

                        event.hook.editOriginal("You got the deathroll").queue()
                    } else {
                        event.hook.editOriginal("You got ${rollsData.last()}").queue()
                    }
                } else {
                    event.hook.editOriginal("Name roulette is not available in this server").queue()
                }
            }
        }
    }
}