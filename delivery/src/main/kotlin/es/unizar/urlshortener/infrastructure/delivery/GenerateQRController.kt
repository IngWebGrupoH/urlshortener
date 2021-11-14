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




interface GenerateQRController {

    /**
     * Generates a QR code from a specified URL.
     *
     */
    fun handleURLToQR(url: String, request: HttpServletRequest): String
}

@RestController
class GenerateQRControllerImpl(
    val createShortUrl : CreateShortUrlUseCaseImpl
    ):GenerateQRController {
        

        @PostMapping("/api/URLToQR")
        override fun handleURLToQR(@RequestParam("url") url: String,request: HttpServletRequest): String {
            val imageData = QRCode(url).render()
            // Save it as a PNG File:
            if(ImageIO.write(imageData, "PNG", File("qr.png"))){
                return "Success"
            }
            return "error generating qr";
        }
    }
    