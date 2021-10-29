package me.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import net.dv8tion.jda.internal.JDAImpl
import java.util.concurrent.CopyOnWriteArrayList

object CoroutineEventManager:InterfacedEventManager() {

    private val listeners = CopyOnWriteArrayList<EventListener>();

    override fun handle(event: GenericEvent) {
        for (listener in listeners) {
            try {
                GlobalScope.launch {
                    listener.onEvent(event);
                }
            }
            catch (throwable:Throwable) {
                JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
                if (throwable is Error) {
                    throw throwable;
                }
            }
        }
    }

}