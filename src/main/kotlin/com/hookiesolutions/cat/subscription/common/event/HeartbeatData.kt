package com.hookiesolutions.cat.subscription.common.event

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData

data class HeartbeatData(
    val status: String,
    val sequence: Long
): SSENotificationData
