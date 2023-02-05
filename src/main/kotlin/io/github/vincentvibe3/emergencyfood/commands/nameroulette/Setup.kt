package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.GenericSubCommand
import io.github.vincentvibe3.emergencyfood.internals.SubCommand
import io.github.vincentvibe3.emergencyfood.serialization.NameRouletteGuild
import io.github.vincentvibe3.emergencyfood.utils.nameroulette.NamerouletteEventLoop
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.util.*

object Setup:GenericSubCommand(), SubCommand {

    private val weekdays = hashMapOf("monday" to 1, "tuesday" to 2, "wednesday" to 3, "thursday" to 4, "friday" to 5, "saturday" to 6, "sunday" to 0)

    override val name: String = "setup"

    override val subCommand: SubcommandData = SubcommandData(name, "Sets up name roulette in this server with updates in this channel or updates the channel and time")
        .addOption(OptionType.STRING, "weekday", "Weekday to send updates on", true)
        .addOption(OptionType.INTEGER, "hour", "Hour to send updates on(0-23)",true)
        .addOption(OptionType.INTEGER, "minute", "Minute to send updates on(0-59)", true)
        .addOption(OptionType.INTEGER, "timezone", "your timezone", true)

    suspend fun checkIfExists(id:String): Boolean {
        val result = Supabase.select("guilds", listOf(
            SupabaseFilter("id", id, SupabaseFilter.Match.EQUALS)
        ))
        return result != "[]"
    }

    private fun adjustTime(weekday:String, hour:Int, timeZone:Int): Pair<Int, Int>? {
        if (!weekdays.contains(weekday)){
            return null
        }
        val hourUTC = hour-timeZone
        if (hourUTC>=24){
            val day = weekdays[weekday]
            val adjustedDay = if (day!=null){
                 if (day==6){
                    0
                } else {
                    day+1
                }
            } else {
                null
            }
            val adjustedHour = hourUTC-24
            return  Pair(adjustedDay!!, adjustedHour)
        } else {
            return Pair(weekdays[weekday]!!, hourUTC)
        }
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guild = event.guild
        val weekday = event.getOption("weekday")?.asString?.lowercase(Locale.getDefault())
        val hour = event.getOption("hour")?.asInt
        val minute = event.getOption("minute")?.asInt
        val timeZone = event.getOption("timezone")?.asInt
        if(timeZone!=null&&hour!=null&&weekday!=null&&minute!=null){
            val adjusted = adjustTime(weekday, hour, timeZone)
            if (guild != null&&adjusted!=null){
                val adjustedDay = adjusted.first
                val adjustedhour = adjusted.second
                if (weekdays.values.contains(adjustedDay)&&adjustedhour in 0..23&&minute in 0..59) {
                    var update = false
                    val guildData = NameRouletteGuild(
                        guild.id,
                        event.channel.id,
                        adjustedDay,
                        adjustedhour,
                        minute
                    )
                    val result = if (checkIfExists(guild.id)){
                        update = true
                        Supabase.update(
                            "guilds",
                            Json.encodeToString(NameRouletteGuild.serializer(), guildData),
                            listOf(
                                SupabaseFilter("id", guild.id, SupabaseFilter.Match.EQUALS)
                            )
                        )
                    } else {
                        Supabase.insert(
                            "guilds",
                            Json.encodeToString(NameRouletteGuild.serializer(), guildData)
                        )
                    }
                    if (result==null||(result.contains("message")&&!result.contains("last_message"))){
                        event.reply("An error occurred").setEphemeral(true).queue()
                    } else if (update) {
                        event.reply("Name roulette has been updated").setEphemeral(true).queue()
                    } else {
                        event.reply("Name roulette is now setup").setEphemeral(true).queue()
                    }
                } else {
                    event.reply("One of the options is not a valid").setEphemeral(true).queue()
                }
                NamerouletteEventLoop.setup()
            } else {
                event.reply("This can only be done in a server").setEphemeral(true).queue()
            }
        }
    }
}