package io.github.vincentvibe3.emergencyfood.utils.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.ConnectException

class LogflareAppender: AppenderBase<ILoggingEvent>() {

    companion object  {
        val client = HttpClient()
        var connectionFailed = false
    }

    override fun append(eventObject: ILoggingEvent?) {
        if (eventObject != null&&!connectionFailed&&Config.logflareKey.isNotBlank()&&Config.logflareUrl.isNotBlank()){
            runBlocking {
                launch {
                    try {
                        val bodyString = JSONObject()
                            .put("message", eventObject.formattedMessage)
                            .put(
                                "metadata",
                                JSONObject()
                                    .put("level", eventObject.level)
                                    .put("loggerName", eventObject.loggerName)
                                    .put("thread", eventObject.threadName)
                                    .put("timeStamp", eventObject.timeStamp)
                                    .put("environment", Config.envName)
                            ).toString()
                        client.post(Config.logflareUrl){
                            body = bodyString
                            headers{
                                append("X-API-KEY", Config.logflareKey)
                                append("Content-Type", "application/json")
                            }

                        }
                    } catch (e: ConnectException){
                        e.printStackTrace()
                        connectionFailed = true
                    } catch (e: RedirectResponseException){
                        e.printStackTrace()
                        connectionFailed = true
                    } catch (e: ClientRequestException){
                        e.printStackTrace()
                        connectionFailed = true
                    } catch (e: ServerResponseException){
                        e.printStackTrace()
                        connectionFailed = true
                    }
                }
            }
        }

    }
}