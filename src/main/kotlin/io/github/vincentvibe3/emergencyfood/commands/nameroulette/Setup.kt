package io.github.vincentvibe3.emergencyfood.commands.nameroulette

import io.github.vincentvibe3.emergencyfood.internals.*
import io.github.vincentvibe3.emergencyfood.utils.supabase.Supabase
import io.github.vincentvibe3.emergencyfood.utils.supabase.SupabaseFilter
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.util.*
import kotlin.collections.HashMap

object Setup:GenericSubCommand(), SubCommand {

    private val weekdays = hashMapOf("monday" to 1, "tuesday" to 2, "wednesday" to 3, "thursday" to 4, "friday" to 5, "saturday" to 6, "sunday" to 0)

    override val name: String = "setup"

    override val subCommand: SubcommandData = SubcommandData(name, "Sets up name roulette in this server with updates in this channel or updates the channel and time")
        .addOption(OptionType.STRING, "weekday", "Weekday to send updates on", true)
        .addOption(OptionType.INTEGER, "hour", "Hour to send updates on(0-23)",true)
        .addOption(OptionType.INTEGER, "minute", "Minute to send updates on(0-59)", true)

    suspend fun checkIfExists(id:String): Boolean {
        val result = Supabase.select("guilds", listOf(
            SupabaseFilter("id", id, SupabaseFilter.Match.EQUALS)
        ))
        return result != "[]"
    }

    override suspend fun handle(event: SlashCommandInteractionEvent) {
        val guild = event.guild
        val weekday = event.getOption("weekday")?.asString?.lowercase(Locale.getDefault())
        val hour = event.getOption("hour")?.asInt
        val minute = event.getOption("minute")?.asInt

        if (guild != null){
            if (weekdays.keys.contains(weekday)&&hour in 0..23&&minute in 0..59) {
                var update = false
                val result = if (checkIfExists(guild.id)){
                    update = true
                    Supabase.update(
                        "guilds",
                        hashMapOf(
                            "channel_id" to event.channel.id,
                            "ping_day_of_week" to weekdays[weekday],
                            "ping_hour" to hour,
                            "ping_min" to minute
                        ) as HashMap<String, Any>,
                        listOf(
                            SupabaseFilter("id", guild.id, SupabaseFilter.Match.EQUALS)
                        )
                    )
                } else {
                     Supabase.insert(
                        "guilds",
                        hashMapOf(
                            "id" to guild.id,
                            "channel_id" to event.channel.id,
                            "ping_day_of_week" to weekdays[weekday],
                            "ping_hour" to hour,
                            "ping_min" to minute
                        ) as HashMap<String, Any>
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
        } else {
            event.reply("This can only be done in a server").setEphemeral(true).queue()
        }
    }
}