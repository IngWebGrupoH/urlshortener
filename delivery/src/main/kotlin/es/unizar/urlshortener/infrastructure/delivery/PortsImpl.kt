package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import es.unizar.urlshortener.core.SafeAndReacheableService
import org.json.JSONObject

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.http.RequestEntity.post
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
/**
 * Implementation of the port [ValidatorService].
 */
class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String)  =urlValidator.isValid(url)
    


    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }
}

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}

class SafeAndReacheableServiceImpl : SafeAndReacheableService {
    override fun isSafe(url: String): Boolean {

        val apiKey: String = "AIzaSyAT03r8yFpf4-FsxV2_wz7iKXOdBfsupsw"

        val ResourceUrl: String = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + apiKey ;
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf("MALWARE", "SOCIAL_ENGINEERING"),
            "platformTypes" to listOf("WINDOWS"),
            "threatEntryTypes" to listOf("URL"),
            "threatEntries" to listOf(mapOf("url" to url)))
        val data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)).toString()

        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(
                URI.create(ResourceUrl))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.body().toString().equals("{}\n")){
            return true
        }
       return false
    }

    override fun isReacheable(url:String): Boolean {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setConnectTimeout(2000)
            return connection.responseCode == 200
        } catch (e: Exception) {
            return false
        }
    }
}

    

