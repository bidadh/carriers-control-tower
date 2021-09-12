package com.hookiesolutions.cat.subscription.service

import com.hookiesolutions.cat.subscription.common.event.CountEvent
import com.hookiesolutions.cat.subscription.common.event.sse.GenericSSENotificationData
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.domain.EnterpriseRepository
import com.hookiesolutions.cat.subscription.domain.Event
import org.slf4j.Logger
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.FluxSink
import java.util.UUID

@Service
class InitService(
    private val enterpriseRepository: EnterpriseRepository,
    private val eventService: EventService,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val log: Logger
) {
  fun init(sink: FluxSink<ServerSentEvent<SSENotificationData>>, enterpriseId: String) {
    sendEventTypes(sink)
    sendCounts(sink, enterpriseId)
    sendUniqueNumberOfAssetsWithEvents(sink, enterpriseId)
    sendEnterpriseLocationEvents(sink, enterpriseId)
  }

  private fun sendEventTypes(sink: FluxSink<ServerSentEvent<SSENotificationData>>) {
    val eventTypes = enumValues<LocationEventAction>()
    val event = ServerSentEvent.builder<SSENotificationData>()
        .event("EventTypesEvent")
        .data(GenericSSENotificationData(eventTypes))
        .comment("eventTypes")
        .id(UUID.randomUUID().toString())
        .build()
    sink.next(event)
  }

  private fun sendCounts(sink: FluxSink<ServerSentEvent<SSENotificationData>>, enterpriseId: String) {
    mongoTemplate.count(query(Asset.Companion.Queries.enterpriseId(enterpriseId)), Asset::class.java)
        .zipWith(mongoTemplate.count(query(DevicePositionUpdate.Companion.Queries.enterpriseId(enterpriseId)), DevicePositionUpdate::class.java))
        .zipWith(mongoTemplate.count(query(Event.Companion.Queries.enterpriseId(enterpriseId)), Event::class.java))
        .subscribe {
          val data = CountEvent(it.t1.t1, it.t1.t2, it.t2)
          log.info("Sending counts: '{}'", data)
          val assetCount = ServerSentEvent.builder<SSENotificationData>()
              .event("CountEvent")
              .data(data)
              .comment("CountEvent")
              .id(UUID.randomUUID().toString())
              .build()
          sink.next(assetCount)
        }
  }

  private fun sendUniqueNumberOfAssetsWithEvents(sink: FluxSink<ServerSentEvent<SSENotificationData>>, enterpriseId: String) {
    eventService.uniqueNumberOfAssetsWithEvents(enterpriseId)
        .subscribe {
          val data = SSENotification.uniqueNumberOfAssetsWithEvents(it)
          val event = ServerSentEvent.builder<SSENotificationData>()
              .event(data.type)
              .data(data.data)
              .comment(data.type)
              .id(UUID.randomUUID().toString())
              .build()
          sink.next(event)
        }
  }

  private fun sendEnterpriseLocationEvents(sink: FluxSink<ServerSentEvent<SSENotificationData>>, enterpriseId: String) {
    enterpriseRepository.findById(enterpriseId)
        .map { SSENotification.enterpriseLocationEvents(it) }
        .subscribe { data ->
          val event = ServerSentEvent.builder<SSENotificationData>()
              .event(data.type)
              .data(data.data)
              .comment(data.type)
              .id(UUID.randomUUID().toString())
              .build()
          sink.next(event)
        }
  }
}
