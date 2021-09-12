package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.common.event.sse.GenericSSENotification
import org.slf4j.Logger
import org.springframework.boot.availability.AvailabilityChangeEvent
import org.springframework.boot.availability.ReadinessState
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.validation.Valid

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 6/7/20 13:49
 */
@RestController
@RequestMapping("/demo")
class DemoController(
    private val log: Logger,
    private val publisher: ApplicationEventPublisher,
    private val sseChannel: MessageChannel
) {
  @PostMapping("/route",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.TEXT_PLAIN_VALUE]
  )
  fun uiRouteUpdate(@RequestBody @Valid request: RouteRequest): Mono<String> {
    val coordinates = request.coordinates
    val asset = request.assetId
    log.info("Sending route to the client for asset: '{}' with '{}' coordinates", asset, coordinates.size)
    val data = mapOf(
        "asset" to asset,
        "route" to request
    )
    val build = MessageBuilder
        .withPayload(GenericSSENotification("AssetRoute", data))
        .build()
    sseChannel.send(build)

    return "OK".toMono()
  }

  @PostMapping("/zoomAt",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.TEXT_PLAIN_VALUE]
  )
  fun finishLegAt(@RequestBody @Valid request: FinishLegRequest): Mono<String> {
    val asset = request.assetId
    val data = mapOf(
        "asset" to asset,
        "points" to request.points
    )
    val build = MessageBuilder
        .withPayload(GenericSSENotification("ZoomAt", data))
        .build()
    sseChannel.send(build)

    return "OK".toMono()
  }

  @PostMapping("/enterprise",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.TEXT_PLAIN_VALUE]
  )
  fun setEnterprise(@Valid @RequestBody request: SelectedEnterpriseRequest): Mono<String> {
    log.info("Setting enterprise: '{}'", request)

    val build = MessageBuilder
        .withPayload(GenericSSENotification("SetEnterprise", request))
        .build()
    sseChannel.send(build)

    return "OK".toMono()
  }

  @GetMapping("/hello")
  fun hello(): String {
    return "Hello World!"
  }

  @GetMapping("/down")
  fun down(): String {
    AvailabilityChangeEvent.publish(publisher, this, ReadinessState.REFUSING_TRAFFIC)
    return "Down"
  }

  @GetMapping("/up")
  fun up(): String {
    AvailabilityChangeEvent.publish(publisher, this, ReadinessState.REFUSING_TRAFFIC)
    return "Up"
  }
}

data class RouteRequest(
  val enterpriseId: String,
  val assetId: String,
  val coordinates: List<List<Double>>,
  val forceFit: Boolean,
  val forceFollow: Boolean
)

data class FinishLegRequest(
  val enterpriseId: String,
  val assetId: String,
  val points: List<List<Double>>
)

data class SelectedEnterpriseRequest(
    val id: String,
    val center: MapPoint,
    val zoom: Int
)

data class MapPoint(
    val lng: Double,
    val lat: Double
)
