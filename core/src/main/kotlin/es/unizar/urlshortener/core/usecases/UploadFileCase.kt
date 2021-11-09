import es.unizar.urlshortener.infrastructure.delivery.UploadCSVController
import org.springframework.web.multipart.MultipartFile

class UploadFileCase(
    private val fileUploadController: UploadCSVController
    ) {
  suspend operator fun invoke(file: MultipartFile) =
  fileUploadController.handleFileUpload(file)
}