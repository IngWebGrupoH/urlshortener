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

import java.net.URI
import javax.servlet.http.HttpServletRequest



interface UploadCSVController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun handleFileUpload(file: MultipartFile, request: HttpServletRequest): CSVResponse
}

@RestController
class UploadCSVControllerImpl(
    val createShortUrl : CreateShortUrlUseCaseImpl
    ):UploadCSVController {
        

        @PostMapping("/api/CSVUpload")
        override fun handleFileUpload(@RequestParam("csv") file: MultipartFile,request: HttpServletRequest): CSVResponse {
            val logger = LoggerFactory.getLogger(this.javaClass)
            logger.info("handling fileupload for {}", file.name)
            val content = String(file.getBytes()).split("\n")
            val shortUrlArray=ArrayList<CSVDataOut>()
            for (i in content) {
                logger.info("url="+i)
                val response=createShortUrl.create(i.subSequence(0, i.length-1).toString(), ShortUrlProperties(
                    ip = request.remoteAddr
                )) 
                shortUrlArray.add(CSVDataOut(URI(i),ShortUrlDataOut(
                    url = URI(response.hash),
                    properties = mapOf(
                        "safe" to response.properties.safe
                    )
                )))
                
            }
            val  strW = StringWriter();
            val writeCSV = CSVWriter(strW);
            for (i in shortUrlArray.iterator()){
                val newLine = arrayOf(i.url.toString(),i.shortUrl.url.toString())
                writeCSV.writeNext(newLine);
            }
            writeCSV.close();
            return CSVResponse.ok(content.toString())
        }
    }
    
    data class CSVResponse(val isSuccess: Boolean,
                               val data: String? = null) {
    
        companion object {
            fun ok(data: String): CSVResponse {
                return CSVResponse(isSuccess = true, data = data)
            }
    
            fun fail(): CSVResponse {
                return CSVResponse(isSuccess = false)
            }
        }
    }
/**
 * Data returned after the creation of a short url.
 */
data class CSVDataOut(
    val url: URI? = null,
    val shortUrl: ShortUrlDataOut
)