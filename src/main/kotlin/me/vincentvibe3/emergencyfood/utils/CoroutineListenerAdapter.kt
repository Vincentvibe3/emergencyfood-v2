package me.vincentvibe3.emergencyfood.utils

import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KClass

interface CoroutineListenerAdapter<T> {

    suspend fun onEvent(event:T)

}