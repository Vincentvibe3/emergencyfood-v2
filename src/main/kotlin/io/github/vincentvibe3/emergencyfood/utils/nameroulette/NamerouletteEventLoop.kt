package io.github.vincentvibe3.emergencyfood.utils.nameroulette

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.InteractionSelectMenu
import io.github.vincentvibe3.emergencyfood.internals.SelectMenuManager
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.MessageBuilder
import org.json.JSONArray
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NamerouletteEventLoop {

    private val guilds = ArrayList<NamerouletteGuildInfo>()

    val activeDropdowns = ArrayList<InteractionSelectMenu>()

    private suspend fun check(guild: NamerouletteGuildInfo){
        val day = guild.day
        val minute = guild.minute
        val hour = guild.hour
        val currentTimeStamp = System.currentTimeMillis()
        val date = Instant.ofEpochSecond(currentTimeStamp/1000)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val dayOfWeekMatch = when (date.dayOfWeek){
            DayOfWeek.MONDAY -> day == 1
            DayOfWeek.TUESDAY -> day == 2
            DayOfWeek.WEDNESDAY-> day == 3
            DayOfWeek.THURSDAY -> day == 4
            DayOfWeek.FRIDAY -> day == 5
            DayOfWeek.SATURDAY -> day == 6
            DayOfWeek.SUNDAY -> day == 0
        }
        val minuteMatch = date.minute == minute
        val hourMatch = date.hour == hour
        val update = dayOfWeekMatch && minuteMatch && hourMatch
        if (update){
            update(guild)
        }
    }

    private suspend fun update(guildData: NamerouletteGuildInfo){
        val client = Bot.getClientInstance()
        val guild = client.getGuildById(guildData.id)
        if (guild!=null){
            val channel = guild.getTextChannelById(guildData.channel_id)
            val rollChoices = getRolls(guildData)
            val deaths = rollChoices.second.shuffled()
            if (deaths.isEmpty()){
                return
            }
            val rolls = updateUsers(guildData, rollChoices.first)
            val deathroll = deaths[0]
            val messageBuilder = MessageBuilder()
            messageBuilder.appendLine("***Name roulette results:***")
            messageBuilder.appendLine("This week's deathroll is $deathroll")
            rolls.forEach {
                messageBuilder.appendLine("<@${it.key}>: ${it.value}")
            }
            if (rolls.isNotEmpty()){
                val message = messageBuilder.build()
                channel?.sendMessage(message)?.queue {
                    runBlocking {
                        launch {
                            Supabase.update(
                                "guilds", hashMapOf(
                                    "last_message" to "${it.id}:${channel.id}",
                                    "current_deathroll" to deathroll
                                ), listOf(SupabaseFilter("id", guild.id, SupabaseFilter.Match.EQUALS))
                            )
                        }
                    }

                }
            }
        }

    }

    private suspend fun getRolls(guild: NamerouletteGuildInfo): Pair<ArrayList<String>, ArrayList<String>> {
        val rawData = Supabase.select("nameroulette_choices", listOf(
            SupabaseFilter("guild", guild.id.toString(), SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = JSONArray(rawData)
        val normal = ArrayList<String>()
        val deathrolls = ArrayList<String>()
        for (index in 0 until jsonData.length()){
            val entry = jsonData.getJSONObject(index)
            if (!entry.getBoolean("deathroll")){
                normal.add(entry.getString("name"))
            } else {
                deathrolls.add(entry.getString("name"))
            }
        }
        return Pair(normal, deathrolls)
    }

    private suspend fun updateUsers(guild: NamerouletteGuildInfo, rollChoices:List<String>): HashMap<String, String> {
        val result = HashMap<String, String>()
        val rawData = Supabase.select("users", listOf(
            SupabaseFilter("guild", guild.id.toString(), SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = JSONArray(rawData)
        for (index in 0 until jsonData.length()){
            val user = jsonData.getJSONObject(index)
            val roll = rollChoices.shuffled()[0]
            Supabase.update("users", hashMapOf(
                "roll_count" to 1,
                "roll_names" to JSONArray().put(roll).toString(),
                "deathroll" to false
            ), listOf(SupabaseFilter("id", user.getString("id"), SupabaseFilter.Match.EQUALS)))
            result[user.getString("id").split(":")[0]] = roll
        }
        return result
    }

    private suspend fun setup(){
        val rawData = Supabase.select("guilds")
        val jsonData = JSONArray(rawData)
        for (index in 0 until jsonData.length()){
            val guildData = jsonData.getJSONObject(index)
            val guild = NamerouletteGuildInfo(
                guildData.getLong("id"),
                guildData.getString("channel_id"),
                guildData.getInt("ping_day_of_week"),
                guildData.getInt("ping_hour"),
                guildData.getInt("ping_min")
            )
            guilds.add(guild)
        }
    }

    private fun cleanDropdowns(){
        activeDropdowns.forEach{
            val time = System.currentTimeMillis()
            if (it.expires){
                val expiry = it.expiry
                if (expiry!=null){
                    if (expiry>=time){
                        activeDropdowns.remove(it)
                        SelectMenuManager.unregisterLocal(it)
                    }
                }
            }
        }
    }

    suspend fun startLoop() {
        var elapsed = 0
        while (true) {
            if (elapsed == 15||elapsed==0){
                setup()
                elapsed = 0
            }
            guilds.forEach {
                check(it)
                cleanDropdowns()
            }
            delay(60000L)
            elapsed++
        }
    }
}