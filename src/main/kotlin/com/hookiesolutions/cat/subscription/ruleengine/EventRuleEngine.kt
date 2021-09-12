package com.hookiesolutions.cat.subscription.ruleengine

import com.hookiesolutions.cat.subscription.config.stream.Bindings.Companion.Channels.Companion.Input.Companion.DEVICE_POSITIONS_INPUT_CHANNEL
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import org.slf4j.Logger
import org.springframework.integration.annotation.Router
import org.springframework.stereotype.Service

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 00:42
 */
@Service
class EventRuleEngine(
    private val rules: List<EventRule>,
    private val log: Logger
) {
  @Router(
      inputChannel = DEVICE_POSITIONS_INPUT_CHANNEL,
      applySequence = "false",
      ignoreSendFailures = "true",
      defaultOutputChannel = "logChannel"
  )
  fun applyRulesToDevicePositionUpdate(payload: DevicePositionUpdate): List<String> {
    log.info("Checking rules for the update: '{}'", payload.deviceId)

    return rules
        .filter { it.accept(payload) }
        .map { it.channel }
  }
}
