package com.hookiesolutions.cat.subscription.ruleengine

import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import org.springframework.integration.core.GenericSelector

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 00:41
 */
interface EventRule: GenericSelector<DevicePositionUpdate> {
  val channel: String
  fun process(payload: DevicePositionUpdate)
}
