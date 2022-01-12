package es.unizar.urlshortener.infrastructure.delivery

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import es.unizar.urlshortener.core.ClickProperties
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.servlet.http.HttpServletRequest


/**
 * The specification of the controller.
 */
interface UrlShortenerController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void>

    /**
     * Creates a short url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut>

}

/**
 * Data required to create a short url.
 */
data class ShortUrlDataIn(
    val url: String,
    val sponsor: String? = null
)

/**
 * Data returned after the creation of a short url.
 */
data class ShortUrlDataOut(
    val url: URI? = null,
    val properties: Map<String, Any> = emptyMap()
)


/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class UrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase
) : UrlShortenerController {

    val factory = JsonNodeFactory.instance

    var meterRegistryG: MeterRegistry? = null


    private lateinit var totalLinkGenerated: Counter



    private lateinit var totalShortenerPetitions: Counter

    private lateinit var totalLinkUse: Counter

    private lateinit var totalLinkError: Counter

    private lateinit var currentConversions: AtomicInteger

    private lateinit var timer: Timer


    @Autowired
    fun setCounter(meterRegistry: MeterRegistry) {
        //counters -> increment value
        meterRegistryG = meterRegistry
        totalLinkGenerated = meterRegistry.counter("URLservice.genlink.counter")
        totalShortenerPetitions = meterRegistry.counter("URLservice.servicePetition.counter")
        totalLinkUse = meterRegistry.counter("URLservice.redirect.counter")
        totalLinkError = meterRegistry.counter("URLservice.genlinkFail.counter")


        //gauges -> shows the current value of a meter.
        currentConversions = meterRegistry.gauge("URLservice.workInProgress", AtomicInteger())!!

        //timer -> measures the time taken for short tasks and the count of these tasks.
        timer = meterRegistry.timer("service.message.long.operation.run.timer")

    }

    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.target)
            totalLinkUse.increment();
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.mode))
        }

    @PostMapping("/api/link", consumes = [ MediaType.APPLICATION_FORM_URLENCODED_VALUE ])
    override fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut> =
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor
            )
        ).let {
            val startTime = System.nanoTime()
            val h = HttpHeaders()
            val url = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            h.location = url
            totalShortenerPetitions.increment()
            val response = ShortUrlDataOut(
                url = url,
                properties = mapOf(
                    "safe" to it.properties.safe
                )
            )
            timer.record(System.nanoTime()-startTime, TimeUnit.NANOSECONDS)
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }

    @GetMapping("/metrics")
    fun fetchMetricsFromMicrometer(): ObjectNode? {
        val metrics = factory.objectNode()
        for (meter in meterRegistryG?.meters!!) {
                val name = meter.id.name
                if (!name.startsWith("URLservice")) continue
                when (meter.id.type) {
                    Meter.Type.COUNTER -> {
                        metrics.put(
                            "counter.$name",
                            meterRegistryG?.get(name)?.counter()?.count()
                        )
                        continue
                    }
                    Meter.Type.GAUGE -> {
                        metrics.put(
                            "gauge.$name",
                            meterRegistryG?.get(name)?.gauge()?.value()
                        )
                    }
                    Meter.Type.TIMER -> {
                        metrics.put(
                            "timer.$name",
                            meterRegistryG?.get(name)?.timer()?.mean(TimeUnit.MILLISECONDS)
                        )
                    }
                }
            }

        return metrics
    }

}
