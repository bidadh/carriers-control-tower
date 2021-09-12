package com.hookiesolutions.cat.subscription.config

import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono
import java.time.Instant


/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:54
 */
@Configuration
class WebConfig constructor(
    private val logger: Logger
) {
  @Bean
  fun corsFilter(): CorsWebFilter {
    val config = CorsConfiguration()

    config.allowCredentials = true
    config.addAllowedOrigin(FE_DEV_ORIGIN_8100)
    config.addAllowedOrigin(FE_DEV_ORIGIN_8200)
    config.addAllowedHeader("*")
    config.addAllowedMethod("*")

    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", config)

    return CorsWebFilter(source)
  }

  @Bean
  @Order(-1)
  fun logGatewayRequest(): WebFilter {
    return WebFilter { exchange, chain ->
      val start = Instant.now()
      return@WebFilter chain.filter(exchange)
          .then(Mono.fromRunnable<Void> {
            val finish = Instant.now()
            val request = exchange.request
            val response = exchange.response
            logger.trace("\"${request.methodValue} ${request.uri} ${request.id}\" {} {} ms", response.statusCode?.value(), finish.toEpochMilli() - start.toEpochMilli())
          })
    }
  }

  companion object {
    const val FE_DEV_ORIGIN_8100 = "http://localhost:8100"
    const val FE_DEV_ORIGIN_8200 = "http://localhost:8200"
  }
}
