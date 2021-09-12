package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.common.request.DevicePositionUpdateRequest
import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.service.PositionService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/6/20 02:41
 */
@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(PositionController.REQUEST_MAPPING)
@Validated
class PositionController(
    private val positionService: PositionService,
    private val log: Logger
) {
  @PostMapping("",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.TEXT_PLAIN_VALUE]
  )
  /*
  current flow:
  POST /position/{deviceId} ->
    publish message to Kafka ( Confluent ) ->
      MongoDB Kafka connector -> MongoDB ( location collection ) ->
        Spring Stream Kafka ServiceActivator: devicePositionsInputChannel ->
          update Asset with position ->
            - Flows::startedMovingAssetsFlow if is first location
            - Flows::assetLocationsFlow to handle locations
   */
  fun create(
      @Valid @RequestBody devicePositionUpdate: DevicePositionUpdateRequest
  ): Mono<String> {
    val deviceId = devicePositionUpdate.deviceId
    val enterpriseId = devicePositionUpdate.enterpriseId
    log.info("Position update received for device: '{}', en: '{}'", deviceId, enterpriseId)
    return positionService.publishPositionFor(enterpriseId, deviceId, devicePositionUpdate)
  }

  companion object {
    const val REQUEST_MAPPING = "/position"
  }
}
