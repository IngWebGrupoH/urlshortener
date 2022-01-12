package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.util.Date
import io.micrometer.core.instrument.Counter
import java.util.concurrent.atomic.AtomicInteger
import org.springframework.beans.factory.annotation.Autowired
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import java.util.concurrent.TimeUnit

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties): ShortUrl
}

/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService,
    private val isSafeAndReacheableService: SafeAndReacheableService
) : CreateShortUrlUseCase {
    private lateinit var totalLinkGenerated: Counter
    private lateinit var currentConversions: AtomicInteger
    private lateinit var timer: Timer
    private lateinit var totalLinkError: Counter
    @Autowired
    fun setCounter(meterRegistry: MeterRegistry) {
        //counters -> increment value
        totalLinkGenerated = meterRegistry.counter("URLservice.genlink.counter")
        totalLinkError = meterRegistry.counter("URLservice.genlinkFail.counter")
        timer = meterRegistry.timer("URLservice.operation.run.timer")

        //gauges -> shows the current value of a meter.
        currentConversions = meterRegistry.gauge("URLservice.workInProgress", AtomicInteger())!!

    }
    override fun create(url: String, data: ShortUrlProperties):ShortUrl{ 
        currentConversions.getAndAdd(1)
        val startTime = System.nanoTime()
        if (validatorService.isValid(url) && isSafeAndReacheableService.isReacheable(url)
            && isSafeAndReacheableService.isSafe(url)  ) {
                totalLinkGenerated.increment()
            val id: String = hashService.hasUrl(url)
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor
                )
            )
            shortUrlRepository.save(su)
            // if(shortUrlRepository.findByKey(su.hash)?.hash!=null){
                
            // }
            currentConversions.decrementAndGet()
            timer.record(System.nanoTime()-startTime, TimeUnit.NANOSECONDS)
            return su;
        } else {
            totalLinkError.increment()
            throw InvalidUrlException(url)
        }
    }
}
