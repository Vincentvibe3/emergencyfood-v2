package io.github.vincentvibe3.emergencyfood.utils.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.internals.ConfigLoader
import io.github.vincentvibe3.emergencyfood.serialization.LogflareMessage
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.net.ConnectException

class LogflareAppender: AppenderBase<ILoggingEvent>() {

    private var connectionFailed = false

    override fun append(eventObject: ILoggingEvent?) {
        if (eventObject != null&&!connectionFailed&&Config.logflareKey.isNotBlank()&&Config.logflareUrl.isNotBlank()&&Config.channel==ConfigLoader.Channel.STABLE){
            runBlocking {
                launch {
                    try {
                        val message = LogflareMessage(
                            eventObject.formattedMessage,
                            LogflareMessage.LogflareMetadata(
                                eventObject.level.levelStr,
                                eventObject.loggerName,
                                eventObject.threadName,
                                eventObject.timeStamp,
                                Config.envName
                            )
                        )
                        val bodyString = Json.encodeToString(LogflareMessage.serializer(), message)
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