package io.github.vincentvibe3.emergencyfood.utils

import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong
import kotlin.properties.Delegates
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URISyntaxException

/**
 * Handles request while rate-limiting
 */
object RequestHandler {

    private val queue = ConcurrentHashMap<String, ConcurrentHashMap<Long, Long>>()

    /**
     * Custom rate-limiting for specific sites or endpoints
     *
     */
    val rateLimits = HashMap<String, Long>()

    //    private val client = HttpClient()
    private val client2 = OkHttpClient()

    private val mutex = Mutex()

    /**
     * Makes a GET request
     *
     * @param originalUrl the URL to call
     * @param headers Headers to use (optional)
     *
     */
    suspend fun get(originalUrl: String, headers:HashMap<String, String> = HashMap()):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                URI(originalUrl).host
            } catch (e:URISyntaxException){
                e.printStackTrace()
                throw RequestFailedException()
            }
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
        var success:Boolean
        try {
            val headersBuilder = Headers.Builder()
            headers.forEach {
                headersBuilder.add(it.key, it.value)
            }
            val request: Request = Request.Builder()
                .url(originalUrl)
                .headers(headersBuilder.build())
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            body = response.body?.string() ?: ""
            success = true
        }catch (e:Exception){
            e.printStackTrace()
            success = false
        }
        if (!success){
            throw RequestFailedException()
        } else {
            return body
        }
    }

    /**
     * Makes a POST request
     *
     * @param originalUrl the URL to call
     * @param headers Headers to use (optional)
     * @param requestBody the request body as a [String]
     *
     */
    suspend fun post(originalUrl: String, requestBody:String, headers:HashMap<String, String> = HashMap()):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                URI(originalUrl).host
            } catch (e:URISyntaxException){
                e.printStackTrace()
                throw RequestFailedException()
            }
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

        var responseBody = ""
        var success:Boolean

        try {
            val headersBuilder = Headers.Builder()
            headers.forEach {
                headersBuilder.add(it.key, it.value)
            }
            val body = requestBody.toRequestBody(null)
            val request: Request = Request.Builder()
                .url(originalUrl)
                .headers(headersBuilder.build())
                .post(body)
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            e.printStackTrace()
            success = false
        }

        if (!success){
            throw RequestFailedException()
        } else {
            return responseBody
        }
    }

    suspend fun patch(originalUrl: String, requestBody:String, headers:HashMap<String, String> = HashMap()):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                URI(originalUrl).host
            } catch (e:URISyntaxException){
                e.printStackTrace()
                throw RequestFailedException()
            }
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

        var responseBody = ""
        var success:Boolean

        try {
            val headersBuilder = Headers.Builder()
            headers.forEach {
                headersBuilder.add(it.key, it.value)
            }
            val body = requestBody.toRequestBody(null)
            val request: Request = Request.Builder()
                .url(originalUrl)
                .headers(headersBuilder.build())
                .patch(body)
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            e.printStackTrace()
            success = false
        }

        if (!success){
            throw RequestFailedException()
        } else {
            return responseBody
        }
    }

    suspend fun delete(originalUrl: String, headers:HashMap<String, String> = HashMap()):String{
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                URI(originalUrl).host
            } catch (e:URISyntaxException){
                e.printStackTrace()
                throw RequestFailedException()
            }
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

        var responseBody = ""
        var success:Boolean

        try {
            val headersBuilder = Headers.Builder()
            headers.forEach {
                headersBuilder.add(it.key, it.value)
            }
            val request: Request = Request.Builder()
                .url(originalUrl)
                .headers(headersBuilder.build())
                .delete()
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            e.printStackTrace()
            success = false
        }

        if (!success){
            throw RequestFailedException()
        } else {
            return responseBody
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