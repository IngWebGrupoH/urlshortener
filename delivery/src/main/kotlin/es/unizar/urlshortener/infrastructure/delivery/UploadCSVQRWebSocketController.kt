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
public class UploadCSVQRWebSocketController(
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
        session.setTextMessageSizeLimit(20000);
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
                ),
                seguro = true
            )))
        }
        for (i in shortUrlArray){
            LOGGER.info("DEBUG: "+"http://localhost:8080/tiny-"+i.shortUrl.url.toString())
            val imageData = QRCode("http://localhost:8080/tiny-"+i.shortUrl.url.toString()).render(cellSize = 5)
            val img = ImageIO.write(imageData, "PNG", File("qr.png"))
            val path = "qr.png"

            val encoded = Files.readAllBytes(Paths.get(path))

            val base64 = Base64.getEncoder().encode(encoded);
            session.sendMessage(TextMessage(base64));
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