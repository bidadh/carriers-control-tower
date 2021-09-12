package com.hookiesolutions.cat.subscription.common.event

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.domain.Event

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 26/6/20 14:07
 */
data class LocationSSENotification(private val eventData: Event): SSENotification {
  override val type: String
    get() = eventData.action.name
  override val data: SSENotificationData
    get() = eventData
}

