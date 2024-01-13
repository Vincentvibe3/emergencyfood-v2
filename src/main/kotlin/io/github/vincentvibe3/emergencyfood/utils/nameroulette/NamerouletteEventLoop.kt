package io.github.vincentvibe3.emergencyfood.utils.nameroulette

import io.github.vincentvibe3.emergencyfood.core.Bot
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.internals.InteractionSelectMenu
import io.github.vincentvibe3.emergencyfood.internals.SelectMenuManager
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteGuild
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteRoll
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteUser
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.ZoneOffset

object NamerouletteEventLoop {

    private val guilds = ArrayList<NameRouletteGuild>()

    val activeDropdowns = ArrayList<InteractionSelectMenu>()

    private suspend fun check(guild: NameRouletteGuild){
        val day = guild.ping_day_of_week
        val minute = guild.ping_min
        val hour = guild.ping_hour
        val date = OffsetDateTime.now(ZoneOffset.UTC)
        val dayOfWeekMatch = when (date.dayOfWeek){
            DayOfWeek.MONDAY -> day == 1
            DayOfWeek.TUESDAY -> day == 2
            DayOfWeek.WEDNESDAY-> day == 3
            DayOfWeek.THURSDAY -> day == 4
            DayOfWeek.FRIDAY -> day == 5
            DayOfWeek.SATURDAY -> day == 6
            DayOfWeek.SUNDAY -> day == 0
            null -> false
        }
        val minuteMatch = date.minute == minute
        val hourMatch = date.hour == hour
        val update = dayOfWeekMatch && minuteMatch && hourMatch
        if (update){
            update(guild)
        }
    }

    private suspend fun update(guildData: NameRouletteGuild){
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
            val messageBuilder = MessageCreateBuilder()
            messageBuilder.addContent("***Name roulette results:***\n")
            messageBuilder.addContent("This week's deathroll is $deathroll\n")
            rolls.forEach {
                messageBuilder.addContent("<@${it.key}>: ${it.value}\n")
            }
            if (rolls.isNotEmpty()){
                val message = messageBuilder.build()
                channel?.sendMessage(message)?.queue {
                    runBlocking {
                        launch {
                            guildData.last_message = "${it.id}:${channel.id}"
                            guildData.current_deathroll = deathroll
                            Supabase.update(
                                "guilds",
                                Json.encodeToString(NameRouletteGuild.serializer(), guildData),
                                listOf(SupabaseFilter("id", guild.id, SupabaseFilter.Match.EQUALS))
                            )
                        }
                    }

                }
            }
        }

    }

    private suspend fun getRolls(guild: NameRouletteGuild): Pair<ArrayList<String>, ArrayList<String>> {
        val rawData = Supabase.select("nameroulette_choices", listOf(
            SupabaseFilter("guild", guild.id.toString(), SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = Json.decodeFromString<List<NameRouletteRoll>>(rawData)
        val normal = ArrayList<String>()
        val deathrolls = ArrayList<String>()
        for (entry in jsonData){
            if (!entry.deathroll){
                normal.add(entry.name)
            } else {
                deathrolls.add(entry.name)
            }
        }
        return Pair(normal, deathrolls)
    }

    private suspend fun updateUsers(guild: NameRouletteGuild, rollChoices:List<String>): HashMap<String, String> {
        val result = HashMap<String, String>()
        val rawData = Supabase.select("users", listOf(
            SupabaseFilter("guild", guild.id.toString(), SupabaseFilter.Match.EQUALS)
        ))
        val jsonData = Json.decodeFromString<List<NameRouletteUser>>(rawData)
        for (user in jsonData){
            val roll = rollChoices.shuffled()[0]
            user.roll_count = 1
            user.roll_names.clear()
            user.roll_names.add(roll)
            user.deathroll = false
            Supabase.update(
                "users",
                Json.encodeToString(NameRouletteUser.serializer(), user),
                listOf(SupabaseFilter("id", user.id, SupabaseFilter.Match.EQUALS))
            )
            result[user.id.split(":")[0]] = roll
        }
        return result
    }

    suspend fun setup(){
        val rawData = Supabase.select("guilds")
        val jsonData = Json.decodeFromString<List<NameRouletteGuild>>(rawData)
	    guilds.clear()
        for (guildData in jsonData){
            guilds.add(guildData)
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
        if (Config.exclusions.contains("nameroulette")){
            return
        }
        var elapsed = 0
        while (true) {
            if (elapsed == 15||elapsed==0){
                setup()
                elapsed = 0
            }
            guilds.forEach {
                println(it)
                check(it)
                cleanDropdowns()
            }
            delay(60000L)
            elapsed++
        }
    }
}
