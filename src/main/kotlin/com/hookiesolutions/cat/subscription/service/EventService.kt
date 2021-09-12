package com.hookiesolutions.cat.subscription.service

import com.hookiesolutions.cat.subscription.domain.Event
import com.hookiesolutions.cat.subscription.domain.EventRepository
import org.slf4j.Logger
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 2/7/20 15:53
 */
@Service
class EventService(
    private val eventRepository: EventRepository,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val log: Logger
) {
  fun saveEvent(event: Event) {
    eventRepository.save(event)
        .subscribe {
          log.info("{} Event saved for asset: '{}'", event.action, event.asset.deviceId)
        }
  }

  fun uniqueNumberOfAssetsWithEvents(event: Event): Mono<Int> {
    return uniqueNumberOfAssetsWithEvents(event.asset.assetType.enterpriseId)
  }

  fun uniqueNumberOfAssetsWithEvents(enterpriseId: String): Mono<Int> {
    val qry = query(Event.Companion.Queries.enterpriseId(enterpriseId))
    return mongoTemplate.findDistinct(qry,"asset.deviceId", Event::class.java, Any::class.java)
        .collectList()
        .map { return@map it.size }
  }
}
