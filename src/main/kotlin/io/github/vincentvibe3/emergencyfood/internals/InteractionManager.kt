package io.github.vincentvibe3.emergencyfood.internals

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface InteractionManager {

    companion object {
        val coroutineScope = CoroutineScope(Dispatchers.Default+ SupervisorJob())
    }

}