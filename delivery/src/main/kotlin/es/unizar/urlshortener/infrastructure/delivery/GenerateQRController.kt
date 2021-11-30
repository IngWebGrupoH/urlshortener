package es.unizar.urlshortener.infrastructure.delivery
import org.slf4j.LoggerFactory
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

import java.io.StringWriter
import com.opencsv.CSVWriter

import java.net.*
import javax.servlet.http.HttpServletRequest

import java.io.File
import javax.imageio.ImageIO
import io.github.g0dkar.qrcode.QRCode
import java.awt.image.BufferedImage
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Arrays
import java.util.Base64

interface GenerateQRController {

    /**
     * Generates a QR code from a specified URL.
     *
     */
    fun handleURLToQR(url: String, request: HttpServletRequest): ResponseEntity<ByteArray>
}



@RestController
class GenerateQRControllerImpl(
    val createShortUrl : CreateShortUrlUseCaseImpl
    ):GenerateQRController {
        

        @GetMapping("/api/URLToQR")
        override fun handleURLToQR(@RequestParam("url") url: String,request: HttpServletRequest): ResponseEntity<ByteArray> {
            val imageData = QRCode(url).render(cellSize = 5)
            val img = ImageIO.write(imageData, "PNG", File("qr.png"))
            val path = "qr.png"

            val encoded = Files.readAllBytes(Paths.get(path))

            //val imageBytes = ByteArrayOutputStream().also { ImageIO.write(imageData, "PNG", it) }.toByteArray()
            //val resource = ByteArrayResource(imageBytes, IMAGE_PNG_VALUE)
            val base64 = Base64.getEncoder().encode(encoded);
            return ResponseEntity(base64, HttpStatus.CREATED)
        }
    }
    