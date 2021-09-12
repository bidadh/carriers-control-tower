package com.hookiesolutions.cat.subscription.ruleengine.rule.property

import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import org.slf4j.Logger
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 13:56
 */
@Component
class SecurityAlertEventRule(
    saveEventChannel: MessageChannel,
    private val log: Logger
): PropertyEventRule(saveEventChannel) {
  override val key: String = SECURITY_ALERT_PROP_KEY
  override val action = LocationEventAction.SECURITY_ALERT

  override fun processEvent() {
    log.info("Processing position update for: '{}', prop value: '{}'", assetInfo.deviceId, value)
  }

  companion object {
    const val SECURITY_ALERT_PROP_KEY = "securityAlert"
  }
}
