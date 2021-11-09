package es.unizar.urlshortener.infrastructure.delivery
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader

@RestController
class UploadCSVController {

    val logger = LoggerFactory.getLogger(UploadCSVController::class.java)

    @PostMapping("/fileupload")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): CSVResponse {
        logger.info("handling fileupload for {}", file.name)
        val content = file.inputStream.bufferedReader().use(BufferedReader::readText)
        logger.info("file content = {}", content)
        return CSVResponse.ok(content)
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