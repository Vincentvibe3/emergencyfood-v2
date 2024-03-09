package io.github.vincentvibe3.emergencyfood.utils

import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import io.github.vincentvibe3.emergencyfood.utils.logging.Logging
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong
import kotlin.properties.Delegates

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
    private val client2 = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    private val mutex = Mutex()

    /**
     * Makes a GET request
     *
     * @param originalUrl the URL to call
     * @param headers Headers to use (optional)
     *
     */
    suspend fun get(originalUrl: String, headers:HashMap<String, String> = HashMap()):String{
        val url = originalUrl.toHttpUrl()
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                url.host
            } catch (e:IllegalArgumentException){
                e.printStackTrace()
                throw RequestFailedException("Url $originalUrl was malformed")
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
                .url(url)
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
            throw RequestFailedException("Failed to GET $originalUrl")
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
        val url = originalUrl.toHttpUrl()
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                url.host
            } catch (e:IllegalArgumentException){
                e.printStackTrace()
                throw RequestFailedException("Url $originalUrl was malformed")
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
                .url(url)
                .headers(headersBuilder.build())
                .post(body)
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            Logging.logger.error(e.message)
            success = false
        }

        if (!success){
            throw RequestFailedException("Failed to POST $originalUrl")
        } else {
            return responseBody
        }
    }

    suspend fun patch(originalUrl: String, requestBody:String, headers:HashMap<String, String> = HashMap()):String{
        val url = originalUrl.toHttpUrl()
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                url.host
            } catch (e:IllegalArgumentException){
                e.printStackTrace()
                throw RequestFailedException("Url $originalUrl was malformed")
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
                .url(url)
                .headers(headersBuilder.build())
                .patch(body)
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            Logging.logger.error(e.message)
            success = false
        }

        if (!success){
            throw RequestFailedException("Failed to PATCH $originalUrl")
        } else {
            return responseBody
        }
    }

    suspend fun delete(originalUrl: String, headers:HashMap<String, String> = HashMap()):String{
        val url = originalUrl.toHttpUrl()
        val host = if (rateLimits.containsKey(originalUrl)){
            originalUrl
        } else {
            try {
                url.host
            } catch (e:IllegalArgumentException){
                Logging.logger.error(e.message)
                throw RequestFailedException("Url $originalUrl was malformed")
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
                .url(url)
                .headers(headersBuilder.build())
                .delete()
                .build()
            val call = client2.newCall(request)
            val response = call.execute()
            responseBody = response.body?.string() ?: ""
            success = true
        } catch (e:Exception){
            Logging.logger.error(e.message)
            success = false
        }

        if (!success){
            throw RequestFailedException("Failed to DELETE $originalUrl")
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