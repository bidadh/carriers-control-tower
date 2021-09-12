package com.hookiesolutions.cat.subscription.common.event

import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import com.hookiesolutions.cat.subscription.domain.EventUpdate
import java.time.LocalDateTime

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 3/7/20 11:37
 */
data class LocationEventUpdate(
    val name: String,
    val action: LocationEventAction,
    val time: LocalDateTime,
    val numberOfAssets: Int
) {
  constructor(name: String, eventUpdate: EventUpdate, numberOfAssets: Int) :
      this(name, eventUpdate.action, eventUpdate.updatedAt, numberOfAssets)
}
