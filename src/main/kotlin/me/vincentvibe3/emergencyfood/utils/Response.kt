package me.vincentvibe3.emergencyfood.utils;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Message;

class Response(private val message: Message){

	fun respond(event:SlashCommandEvent){
		event.hook.editOriginal(message).queue()
	}

}
