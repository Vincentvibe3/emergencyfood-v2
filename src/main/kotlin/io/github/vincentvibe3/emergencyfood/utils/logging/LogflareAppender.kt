package io.github.vincentvibe3.emergencyfood.utils.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.ConnectException

class LogflareAppender: AppenderBase<ILoggingEvent>() {

    private var connectionFailed = false

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

                        RequestHandler.post(
                            Config.logflareUrl,
                            bodyString,
                            hashMapOf("X-API-KEY" to Config.logflareKey, "Content-Type" to "application/json")
                        )
                    } catch (e: ConnectException){
                        e.printStackTrace()
                        connectionFailed = true
                    }
                }
            }
        }

    }
}