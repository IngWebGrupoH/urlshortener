package es.unizar.urlshortener.infrastructure.delivery
import es.unizar.urlshortener.infrastructure.delivery.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    @Autowired
    private lateinit var socketHandler: UploadCSVWebSocketController

    @Autowired
    private lateinit var socketHandlerCSVQR: UploadCSVQRWebSocketController
 
    @Autowired
    private lateinit var socketHandlerCSVStatus: UploadCSVStatusWebSocketController

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(socketHandler, "/websocket/CSVUpload").setAllowedOrigins("*")
        registry.addHandler(socketHandlerCSVQR, "/websocket/CSVUploadQR").setAllowedOrigins("*")
        registry.addHandler(socketHandlerCSVStatus, "/websocket/CSVUploadStatus").setAllowedOrigins("*")
    }
}