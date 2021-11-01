package me.vincentvibe3.emergencyfood.utils

import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong
import kotlin.properties.Delegates

object RequestHandler {

    private val queue = ConcurrentHashMap<String, ConcurrentHashMap<Long, Long>>()
    private val rateLimits = HashMap<String, Long>()
    private val mutex = Mutex()

    suspend fun get(originalUrl: String):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            URI(originalUrl).host
        }
        var queueTime by Delegates.notNull<Long>()
        //sync queue position fetching
        mutex.withLock {
            queueTime = getQueuePos(host)
        }
        while (System.currentTimeMillis()/1000 < queueTime) {
            delay(100L)
        }
        cleanQueue(queueTime)

        var body = ""
        var success = false
        val httpAsync = "http://127.0.0.1:8000"
            .httpGet()
            .responseString { request, response, result ->
                result.fold(
                    {data ->
                        success = true
                        body = data},
                    {error ->
                        success = false}
                )
            }
        httpAsync.join()
        if (!success){
            throw RequestFailedException()
        } else {
            return body
        }
    }

    private fun getQueuePos(entry: String):Long{
        val queueToCheck = queue[entry]
        val currentTime = (System.currentTimeMillis().toDouble() / 1000).roundToLong()
        if (queueToCheck != null) {
            if (queueToCheck.isEmpty()){
                queue[entry]?.set(currentTime, 1)
                return currentTime
            } else {
                val lastTime = queueToCheck.keys.maxOrNull()!!
                if (currentTime > lastTime){
                    queueToCheck[currentTime] = 1
                    return currentTime
                }
                return if (queueToCheck[lastTime]!! == rateLimits.getOrDefault(entry, Templates.defaultRateLimit)){
                    val queuedTime = lastTime+1
                    queueToCheck[queuedTime] = 1
                    queuedTime
                } else {
                    queueToCheck.replace(lastTime, queueToCheck[lastTime]!!+1)
                    lastTime
                }
            }

        } else {
            queue[entry] = ConcurrentHashMap()
            queue[entry]?.set(currentTime, 1)
            return currentTime
        }
    }

    private fun cleanQueue(currentTime:Long){
        queue.forEach { entry ->
            entry.value
                .filter { it.key < currentTime }
                .forEach { entry.value.remove(it.key) }
        }
    }

}