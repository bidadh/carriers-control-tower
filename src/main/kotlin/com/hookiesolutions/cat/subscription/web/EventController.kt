package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.domain.Event
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 2/7/20 16:54
 */

@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(EventController.REQUEST_MAPPING)
class EventController(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val log: Logger
) {
  @GetMapping("/{enterpriseId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun enterpriseEvents(
      @PathVariable enterpriseId: String,
      @RequestParam(required = false, defaultValue = "20") size: Int
  ): Flux<Event> {
    log.info("Fetching all events...")
    val sortBy = Sort.by(Sort.Direction.DESC, "time")
    val qry = query(Event.Companion.Queries.enterpriseId(enterpriseId))
        .with(PageRequest.of(0, size, sortBy))

    return mongoTemplate
        .find(qry, Event::class.java)
  }

  companion object {
    const val REQUEST_MAPPING = "/event"
  }
}
