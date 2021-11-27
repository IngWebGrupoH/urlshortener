package es.unizar.urlshortener.infrastructure.delivery
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.server.standard.ServerEndpointExporter
import java.util.*
import javax.websocket.*
import javax.websocket.CloseReason.CloseCodes
import javax.websocket.server.ServerEndpoint
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.commons.CommonsMultipartFile
import es.unizar.urlshortener.core.*

import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.*

import org.springframework.web.bind.annotation.*
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.*
import javax.servlet.http.HttpServletRequest

import java.io.IOException
import java.io.StringWriter

@Component
public class UploadCSVWebSocketController(
    val createShortUrl :CreateShortUrlUseCase
): TextWebSocketHandler(){

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) { //the messages will be broadcasted to all users.
        println("received connection")
    }
 
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)
        println("Connection closed by client")
    }
    /**
     * Message received
     *
     * @param message
     */
    @Throws(InterruptedException::class, IOException::class)
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        LOGGER.info("Server Message ... Session "+session.id)
        
        val content = message.payload.split("\n")
        if(content.isEmpty()){
            val h = HttpHeaders()
            h.location = null
            val response = CSVResponse(
                data= null,
                isSuccess = false
            )
            session.close(CloseStatus(2, "File empty!"));
        }
        val shortUrlArray=ArrayList<CSVDataOut>()
        for (i in content) {
            val url = URL(i);
            val nullFragment = null;
            val uri = URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            LOGGER.info("url="+uri.toString());
            val response=createShortUrl.create(uri.toString(), ShortUrlProperties(
                ip = "request.remoteAddr"
            )) 
            shortUrlArray.add(CSVDataOut(uri,ShortUrlDataOut(
                url = URI(response.hash),
                properties = mapOf(
                    "safe" to response.properties.safe
                )
            )))
            session.sendMessage(TextMessage(("http://localhost:8080/tiny-"+URI(response.hash).toString())))
            Thread.sleep(500)
        }  
    }
    @OnError
    public fun onError(session: Session, errorReason: Throwable) {
        LOGGER.error("Session ${session.id} closed because of ${errorReason.javaClass.name}", errorReason)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}