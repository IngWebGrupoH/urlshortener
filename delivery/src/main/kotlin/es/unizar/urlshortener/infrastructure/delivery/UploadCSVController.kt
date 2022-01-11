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



interface UploadCSVController {
    fun UploadCSVController(){}
    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun handleFileUpload(csv: MultipartFile, request: HttpServletRequest): ResponseEntity<CSVResponse>
}

@RestController
class UploadCSVControllerImpl(
    val createShortUrl : CreateShortUrlUseCase
    ):UploadCSVController {

        @PostMapping("/api/CSVUpload" , consumes =  ["multipart/form-data"] )
        override fun handleFileUpload(@RequestParam("csv") csv: MultipartFile,request: HttpServletRequest): ResponseEntity<CSVResponse> {
            val logger = LoggerFactory.getLogger(this.javaClass)
            logger.info("handling fileupload for {}", csv.name)
            val content = String(csv.getBytes()).split("\n")
            if(content.isEmpty()){
                val h = HttpHeaders()
                h.location = null
                val response = CSVResponse(
                    data= null,
                    isSuccess = false
                )
                return ResponseEntity<CSVResponse>(response, h, HttpStatus.NO_CONTENT)
            }
            val shortUrlArray=ArrayList<CSVDataOut>()
            for (i in content) {
                val url = URL(i);
                val nullFragment = null;
                val uri = URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
                logger.info("url="+uri.toString())
                val response=createShortUrl.create(uri.toString(), ShortUrlProperties(
                    ip = request.remoteAddr
                )) 
                shortUrlArray.add(CSVDataOut(uri,ShortUrlDataOut(
                    url = URI(response.hash),
                    properties = mapOf(
                        "safe" to response.properties.safe
                    ),
                    seguro = true
                )))
                
            }
            val shorteredUrlArray=ArrayList<String>()
            for (i in shortUrlArray.iterator()){
                shorteredUrlArray.add("http://localhost:8080/tiny-"+i.shortUrl.url.toString())
            }
            val h = HttpHeaders()
            h.location = shortUrlArray.get(0).url
            val response = CSVResponse(
                data= shorteredUrlArray.toString(),
                isSuccess = true
            )
            return ResponseEntity<CSVResponse>(response, h, HttpStatus.OK)
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