package com.hookiesolutions.cat.subscription.common.event

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData

data class CountEvent(
    val asset: Long,
    val position: Long,
    val event: Long
): SSENotificationData
