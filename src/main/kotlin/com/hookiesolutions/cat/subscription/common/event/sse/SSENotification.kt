package com.hookiesolutions.cat.subscription.common.event.sse

import com.hookiesolutions.cat.subscription.common.event.LocationEventUpdate
import com.hookiesolutions.cat.subscription.common.response.AssetResponse
import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.Enterprise

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:19
 */
interface SSENotification {
  val type: String
  val data: SSENotificationData

  companion object {
    fun assetStartedMoving(asset: Asset): SSENotification {
      val value = AssetResponse.from(asset)
      return GenericSSENotification("AssetStartedMoving", GenericSSENotificationData(value))
    }

    fun assetMoved(asset: Asset): SSENotification {
      val value = AssetResponse.from(asset)
      return GenericSSENotification("AssetMoved", GenericSSENotificationData(value))
    }

    fun uniqueNumberOfAssetsWithEvents(count: Int): SSENotification {
      return GenericSSENotification("AssetsWithEventsCount", GenericSSENotificationData(count))
    }

    fun enterpriseLocationEvents(en: Enterprise): SSENotification {
      val value = en.locations
          .filter { it.eventUpdate != null }
          .map { LocationEventUpdate(it.name, it.eventUpdate!!, it.numberOfAssets) }
      return GenericSSENotification("EnterpriseLocationEvents", GenericSSENotificationData(value))
    }
  }
}

interface SSENotificationData

data class GenericSSENotification(
  override val type: String,
  override val data: GenericSSENotificationData
): SSENotification {
  constructor(type: String, data: Any) : this(type, GenericSSENotificationData(data))
}

data class GenericSSENotificationData(
    val value: Any
): SSENotificationData
