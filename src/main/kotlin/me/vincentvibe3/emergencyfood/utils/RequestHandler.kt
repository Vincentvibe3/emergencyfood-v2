package me.vincentvibe3.emergencyfood.utils

import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import java.net.URI
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object RequestHandler {

    private val queue = HashMap<String, HashMap<Long, Long>>()
    private val rateLimits = HashMap<String, Long>()

    fun get(originalUrl: String, id:Int):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            URI(originalUrl).host
        }
        val queueTime = getQueuePos(host, id)
//        println("$queueTime $id")
        runBlocking {
            val job = launch {
                while (System.currentTimeMillis()/1000 < queueTime){
                    delay(500L)
                    println("${System.currentTimeMillis()/1000} $queueTime $id")
                }
            }
            job.join()
        }
        cleanQueue(queueTime)
        var body = ""
        var success = false
        val httpAsync = originalUrl.httpGet()
            .responseString { request, response, result ->
                val (responseText, _) = result
                if (responseText != null && response.statusCode == 200){
                    success = true
                    body = responseText
                } else {
                    success = false
                }
            }
        httpAsync.join()
        if (!success){
            throw RequestFailedException()
        } else {
            return body
        }
    }

    private fun getQueuePos(entry:String, id:Int):Long{
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
            queue[entry] = HashMap()
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