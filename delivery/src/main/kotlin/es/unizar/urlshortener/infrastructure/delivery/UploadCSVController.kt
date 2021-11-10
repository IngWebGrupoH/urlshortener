package es.unizar.urlshortener.infrastructure.delivery
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import java.io.BufferedReader


interface UploadCSVController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): CSVResponse
}
@RestController
class UploadCSVControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase
    ):UploadCSVController {
    val logger = LoggerFactory.getLogger(UploadCSVControllerImpl::class.java)

    @PostMapping("/api/uploadCSV")
    override fun handleFileUpload(@RequestParam("csv") file: MultipartFile): CSVResponse {
        logger.info("handling fileupload for {}", file.name)
        val content = String(file.getBytes());//inputStream.bufferedReader().use(BufferedReader::readText)
        val urlArray=content.split("\n")
        logger.info("file content = {}", urlArray)
        return CSVResponse.ok(urlArray.toString())
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