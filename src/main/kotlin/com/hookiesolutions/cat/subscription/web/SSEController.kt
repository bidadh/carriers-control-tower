package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.common.event.HeartbeatData
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.service.InitService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.SubscribableChannel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Duration
import java.util.UUID

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:12
 */
@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(SSEController.REQUEST_MAPPING)
class SSEController(
    private val sseChannel: SubscribableChannel,
    private val initService: InitService,
    private val log: Logger
) {
  @GetMapping("/{enterpriseId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  fun events(@PathVariable enterpriseId: String): Flux<ServerSentEvent<SSENotificationData>> {
    return Flux.create { sink ->
      val handler = createHandlerUsing(sink, enterpriseId)
      val heartbeatDisposable = createHeartbeatHandlerUsing(sink)
          .subscribe()

      initService.init(sink, enterpriseId)

      sink.onCancel {
        heartbeatDisposable.dispose()
        sseChannel.unsubscribe(handler)
      }
      sseChannel.subscribe(handler)
    }
  }

  private fun createHeartbeatHandlerUsing(sink: FluxSink<ServerSentEvent<SSENotificationData>>): Flux<Long> {
    return Flux.interval(Duration.ofSeconds(40L))
        .doOnNext {
          val sse = ServerSentEvent
              .builder<SSENotificationData>()
              .event("Heartbeat")
              .data(HeartbeatData("UP", it))
              .comment("Heartbeat message")
              .id(UUID.randomUUID().toString())
              .build()
          sink.next(sse)
        }
        .doOnNext { log.debug("Heartbeat SSE sent... ") }
  }

  private fun createHandlerUsing(sink: FluxSink<ServerSentEvent<SSENotificationData>>, enterpriseId: String): MessageHandler {
    return MessageHandler { msg ->
      val notification = msg.payload as SSENotification
      log.info("received msg of type '{}'", notification.type)
      if(log.isDebugEnabled) {
        log.debug("Emitting event '{}'", notification)
      }
      val sse = ServerSentEvent.builder<SSENotificationData>()
          .event(notification.type)
          .data(notification.data)
          .comment(msg.payload.javaClass.name)
          .id(msg.payload.hashCode().toString())
          .build()
      sink.next(sse)
    }
  }

  companion object {
    const val REQUEST_MAPPING = "/events"
  }
}
