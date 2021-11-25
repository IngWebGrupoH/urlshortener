package es.unizar.urlshortener.infrastructure.delivery
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.*
import org.springframework.web.socket.config.annotation.*
import org.springframework.web.socket.handler.AbstractWebSocketHandler

import java.util.concurrent.atomic.AtomicLong
import org.slf4j.LoggerFactory

class User(val id: Long, val name: String)
class Message(val msgType: String, val data: Any)

class UploadCSVWebSocketController : AbstractWebSocketHandler() {

    val sessionList = HashMap<WebSocketSession, User>()
    var uids = AtomicLong(0)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList -= session
    }
    
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = ObjectMapper().readTree(message?.payload)
        val logger = LoggerFactory.getLogger(this.javaClass)
        logger.info("Message type "+json.get("type").asText()+" received");
        // {type: "join/say", data: "name/msg"}
        when (json.get("type").asText()) {
            // "join" -> {
            //     val user = User(uids.getAndIncrement(), json.get("data").asText())
            //     sessionList.put(session!!, user)
            //     // tell this user about all other users
            //     emit(session, Message("shorteredCSV", sessionList.values))
            // }
            "uploadCSV" -> {
                val user = User(uids.getAndIncrement(), json.get("data").asText())
                sessionList.put(session!!, user)
                logger.info(user.name);
                

                emit(session, Message("CSVResult", user.name))
            }
        }
    }

    fun emit(session: WebSocketSession, msg: Message) = session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
    fun broadcast(msg: Message) = sessionList.forEach { emit(it.key, msg) }
    fun broadcastToOthers(me: WebSocketSession, msg: Message) = sessionList.filterNot { it.key == me }.forEach { emit(it.key, msg) }
}

@Configuration @EnableWebSocket
class WSConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(UploadCSVWebSocketController(), "/api").withSockJS()
    }
}