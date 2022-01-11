package es.unizar.urlshortener.infrastructure.delivery
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.*
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

import com.opencsv.CSVWriter

import java.net.*
import kotlinx.serialization.Serializable
import java.io.File
import javax.imageio.ImageIO
import io.github.g0dkar.qrcode.QRCode
import java.awt.image.BufferedImage
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Arrays
import java.util.Base64
import java.io.IOException
import java.io.StringWriter

@Component
public class UploadCSVStatusWebSocketController(
    val createShortUrl :CreateShortUrlUseCase,
    private val validatorService: ValidatorService,
    val isSafeAndReacheableService: SafeAndReacheableService
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
    @Serializable
    data class Data(val b: String)
    @Throws(InterruptedException::class, IOException::class)
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        LOGGER.info("Server Message ... Session "+session.id)
        session.setTextMessageSizeLimit(20000);
        val content = message.payload.split("\n")
        if(content.isEmpty()){
            val h = HttpHeaders()
            h.location = null
            session.close(CloseStatus(2, "File empty!"));
        }
        for (i in content) {
            val url = URL(i);
            val nullFragment = null;
            val uri = URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            session.sendMessage(TextMessage("seguro"));
                        
            if(validatorService.isValid(url.toString()) && isSafeAndReacheableService.isReacheable(url.toString())
            && isSafeAndReacheableService.isSafe(url.toString())  ){
                session.sendMessage(TextMessage("seguro"));
            }else{
                session.sendMessage(TextMessage("no seguro"));
            }
        }
        session.close();
    }
    @OnError
    public fun onError(session: Session, errorReason: Throwable) {
        LOGGER.error("Session ${session.id} closed because of ${errorReason.javaClass.name}", errorReason)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}