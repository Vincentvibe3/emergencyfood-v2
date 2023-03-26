package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteGuild
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteRoll
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteUser
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
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
        val jsonRollData = Json.decodeFromString<List<NameRouletteRoll>>(rawRollData)
        return if (jsonRollData.isEmpty()) {
            null
        } else {
            val index = Random.nextInt(jsonRollData.indices)
            jsonRollData[index].name
        }
    }

    private fun getDeath(rollCount: Int): Boolean {
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
        val guildsDataJson = Json.decodeFromString<List<NameRouletteGuild>>(rawDataGuild)
        return if (guildsDataJson.isEmpty()){
           false
        } else {
            val guildDataJson = guildsDataJson[0]
            val deathroll = guildDataJson.current_deathroll
            messageBuilder.addContent("***Name roulette results:***\n")
            messageBuilder.addContent("This week's deathroll is $deathroll\n")
            val usersData = Json.decodeFromString<List<NameRouletteUser>>(rawData)
            for (user in usersData){
                val rolls = user.roll_names
                if (user.deathroll){
                    rolls.add("Deathroll")
                }
                messageBuilder.addContent("<@${user.id.split(":")[0]}>: ${rolls.joinToString(", ")}\n")
            }
            updateMessage(guildDataJson, messageBuilder.build())
            return true
        }
    }

    private fun updateMessage(guildData: NameRouletteGuild, message: MessageCreateData){
        val lastMessageData = guildData.last_message?.split(":")
        val originalMessageId = lastMessageData?.get(0)
        val channelId = lastMessageData?.get(1)
        val client = Bot.getClientInstance()
        if (originalMessageId != null&&channelId != null) {
            client.getTextChannelById(channelId)
                ?.retrieveMessageById(originalMessageId)
                ?.queue{
                    it.editMessage(MessageEditData.fromCreateData(message)).queue()
                }

        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val rawUserData = Supabase.select(
            "users", listOf(
                SupabaseFilter("id", "${event.user.id}:${event.guild!!.id}", SupabaseFilter.Match.EQUALS)
            )
        )
        val guild = event.guild
        val jsonUserData = Json.decodeFromString<List<NameRouletteUser>>(rawUserData)
        if (jsonUserData.isEmpty()) {
            event.reply("You are not registered").queue()
        } else if (guild == null) {
            event.reply("This cannot be used outside of a server").queue()
        } else {
            val userData = jsonUserData[0]
            val rolls = userData.roll_count
            if (userData.deathroll){
                event.reply("You got the deathroll. You may not reroll").setEphemeral(true).queue()
                return
            }
            if (rolls >= 3) {
                event.reply("You do not have any more rolls").queue()
            } else {
                event.deferReply(true).queue()
                val isDeath = getDeath(rolls)
                val rollsData = userData.roll_names
                userData.roll_count+=1
                if (isDeath){
                    userData.deathroll = true
                } else {
                    var roll = getRoll(guild.id)
                    if (roll == null) {
                        event.reply("No rolls are available").queue()
                        return
                    }
                    while (rollsData.contains(roll)){
                       roll = getRoll(guild.id)
                    }
                    if (roll != null) {
                        rollsData.add(roll)
                    }
                }
                Supabase.update(
                    "users",
                    Json.encodeToString(NameRouletteUser.serializer(), userData),
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