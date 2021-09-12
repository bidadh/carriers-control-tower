package com.hookiesolutions.cat.subscription.common.event

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.domain.Asset

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:34
 */

class CreateAssetEvent(private val asset: Asset): SSENotification {
  override val type: String
    get() = "NewAsset"
  override val data: SSENotificationData
    get() = asset
}
