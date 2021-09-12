package com.hookiesolutions.cat.subscription.service

import com.hookiesolutions.cat.subscription.common.request.DevicePositionUpdateRequest
import com.hookiesolutions.cat.subscription.config.stream.Bindings.Companion.Channels.Companion.Output.Companion.DEVICE_POSITIONS_OUTPUT_CHANNEL
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.domain.PositionRepository
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.slf4j.Logger
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/6/20 02:45
 */
@Service
class PositionService(
    private val timeMachine: TimeMachine,
    private val securityHandler: SecurityHandler,
    private val positionRepository: PositionRepository,
    private val devicePositionsOutputChannel: MessageChannel,
    private val log: Logger
) {
  fun publishPositionFor(
      @Parameter(`in` = ParameterIn.PATH) enterpriseId: String,
      @Parameter(`in` = ParameterIn.PATH) deviceId: String,
      devicePositionUpdateRequest: DevicePositionUpdateRequest
  ): Mono<String> {
    return securityHandler.loggedInUsername()
        .map {
          return@map DevicePositionUpdate(
              deviceId,
              enterpriseId,
              timeMachine.now(),
              it,
              devicePositionUpdateRequest.position,
              devicePositionUpdateRequest.properties
          )
        }
        .doOnNext {
          log.info("Publishing location update message to Kafka....'{}'", it.deviceId)
          devicePositionsOutputChannel
              .send(MessageBuilder.withPayload(it).build())
        }
        .map { "OK" }
  }

  @ServiceActivator(inputChannel = DEVICE_POSITIONS_OUTPUT_CHANNEL)
  fun saveDevicePositionUpdate(dpu: DevicePositionUpdate) {
    log.info("Saving Device Position Update for device: '{}', at: '{}'", dpu.deviceId, dpu.time)
    positionRepository.save(dpu)
        .subscribe {
          log.debug("DPU was updates successfully with Id: '{}'", it.id)
        }
  }
}
