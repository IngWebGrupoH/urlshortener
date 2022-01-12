package es.unizar.urlshortener

import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.*
import es.unizar.urlshortener.infrastructure.repositories.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Counter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */

@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository
) {
    @Bean
    fun clickRepositoryService() = ClickRepositoryServiceImpl(clickEntityRepository)

    @Bean
    fun shortUrlRepositoryService() = ShortUrlRepositoryServiceImpl(shortUrlEntityRepository)

    @Bean
    fun validatorService() = ValidatorServiceImpl()

    @Bean
    fun safeAndReacheableService() = SafeAndReacheableServiceImpl()

    @Bean
    fun hashService() = HashServiceImpl()

    @Bean
    fun redirectUseCase() = RedirectUseCaseImpl(shortUrlRepositoryService())

    @Bean
    fun logClickUseCase() = LogClickUseCaseImpl(clickRepositoryService())

    @Bean
    fun createShortUrlUseCase() = CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), validatorService(), hashService(), safeAndReacheableService())
    
    @Bean
    fun meterRegistry() = SimpleMeterRegistry();
}
