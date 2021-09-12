package com.hookiesolutions.cat.subscription.web

import org.slf4j.Logger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.validation.Valid

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 27/6/20 18:02
 */
@RestController
class SubscriptionController(
    private val log: Logger
) {
  @PostMapping("/subscription/handleEvent")
  fun handleEvent(@Valid @RequestBody eventData: Any): Mono<String> {
    log.info("'{}'", eventData)
    return "OK".toMono()
  }
}

