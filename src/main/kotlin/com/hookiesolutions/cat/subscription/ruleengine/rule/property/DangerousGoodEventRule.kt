package com.hookiesolutions.cat.subscription.ruleengine.rule.property

import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import org.slf4j.Logger
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 00:45
 */
@Component
class DangerousGoodEventRule(
    saveEventChannel: MessageChannel,
    private val log: Logger
): PropertyEventRule(saveEventChannel) {
  override val key: String = DANGEROUS_GOOD_PROP_KEY
  override val action = LocationEventAction.DG_ALERT

  override fun processEvent() {
    log.info("Processing '{}'", assetInfo.deviceId)
  }

  companion object {
    const val DANGEROUS_GOOD_PROP_KEY = "dangerousGood"
  }
}
