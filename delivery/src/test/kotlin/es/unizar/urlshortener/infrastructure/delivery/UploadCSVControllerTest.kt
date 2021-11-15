package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.context.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.junit.jupiter.api.Assertions.*
import org.json.JSONString

import javax.servlet.http.HttpServletRequest
import java.io.FileInputStream;
import java.io.File;
import com.fasterxml.jackson.databind.util.JSONPObject


val testUrls = "http://www.unizar.es/\nhttps://gradle.com/\n"

@WebMvcTest
@ContextConfiguration(classes = [
    UrlShortenerControllerImpl::class,
    RestResponseEntityExceptionHandler::class])
class UploadCSVControllerTest() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @MockBean
    private lateinit var redirectUseCase: RedirectUseCase

    @MockBean
    private lateinit var logClickUseCase: LogClickUseCase

    @MockBean
    private lateinit var createShortUrlUseCase: CreateShortUrlUseCase

    // @Test
    // fun `handleFileUpload process an empty file`() {
    //     val file = MockMultipartFile("csv","urls.csv",MediaType.TEXT_PLAIN_VALUE,"".toByteArray());
    //     val response = CSVResponse(
    //         data= null,
    //         isSuccess = false
    //     )
    //     val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    //     val builder =
    //             MockMvcRequestBuilders.multipart("/api/CSVUpload")
    //                 .contentType(MediaType.MULTIPART_FORM_DATA)
    //                 .param("csv",testUrls)
    //                 .characterEncoding("utf-8");
                    
    //     mockMvc.perform(builder)
    //                 .andDo(print())
    //                 .andExpect(status().isNoContent)
    //                 .andExpect(content().string(response.toString()));
    // }
    // @Test
    // fun `handleFileUpload process a CSV correctly`() {
    //     val file = MockMultipartFile("csv","urls.csv",MediaType.TEXT_PLAIN_VALUE,testUrls.toByteArray());
    //     val response = CSVResponse(
    //         data= null,
    //         isSuccess = false
    //     )
    //     val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    //     val builder =
    //             MockMvcRequestBuilders.multipart("/api/CSVUpload")
    //             .contentType(MediaType.MULTIPART_FORM_DATA)
    //             .content(file.getBytes())
    //             .characterEncoding("utf-8");

    //     mockMvc.perform(builder)
    //                 .andDo(print())
    //                 .andExpect(status().isOk);
    // }
    // @Test
    // fun `redirectTo returns a redirect when the key exists`() {
    //     given(redirectUseCase.redirectTo("key")).willReturn(Redirection("http://example.com/"))

    //     mockMvc.perform(get("/tiny-{id}", "key"))
    //         .andExpect(status().isTemporaryRedirect)
    //         .andExpect(redirectedUrl("http://example.com/"))

    //     verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
    // }

    // @Test
    // fun `redirectTo returns a not found when the key does not exist`() {
    //     given(redirectUseCase.redirectTo("key"))
    //         .willAnswer { throw RedirectionNotFound("key") }

    //     mockMvc.perform(get("/tiny-{id}", "key"))
    //         .andDo(print())
    //         .andExpect(status().isNotFound)
    //         .andExpect(jsonPath("$.statusCode").value(404))

    //     verify(logClickUseCase, never()).logClick("key", ClickProperties(ip = "127.0.0.1"))
    // }

    // @Test
    // fun `creates returns a basic redirect if it can compute a hash`() {
    //     given(createShortUrlUseCase.create(
    //         url = "http://example.com/",
    //         data = ShortUrlProperties(ip = "127.0.0.1")
    //     )).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/")))

    //     mockMvc.perform(post("/api/link")
    //         .param("url", "http://example.com/")
    //         .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    //         .andDo(print())
    //         .andExpect(status().isCreated)
    //         .andExpect(redirectedUrl("http://localhost/tiny-f684a3c4"))
    //         .andExpect(jsonPath("$.url").value("http://localhost/tiny-f684a3c4"))
    // }

    // @Test
    // fun `creates returns bad request if it can compute a hash`() {
    //     given(createShortUrlUseCase.create(
    //         url = "ftp://example.com/",
    //         data = ShortUrlProperties(ip = "127.0.0.1")
    //     )).willAnswer { throw InvalidUrlException("ftp://example.com/") }

    //     mockMvc.perform(post("/api/link")
    //         .param("url", "ftp://example.com/")
    //         .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    //         .andExpect(status().isBadRequest)
    //         .andExpect(jsonPath("$.statusCode").value(400))
    // }
}