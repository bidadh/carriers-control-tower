package com.hookiesolutions.cat.subscription.common.response

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.AssetTypeInfo
import com.hookiesolutions.cat.subscription.domain.PositionUpdate

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 2/7/20 12:50
 */
data class AssetResponse(
    val name: String,
    val deviceId: String,
    val assetType: AssetTypeInfo,
    val currentPosition: PositionUpdate,
    val numberOfPositionUpdates: Int
) : SSENotificationData {
  companion object {
    fun from(asset: Asset): AssetResponse {
      return AssetResponse(
          asset.name,
          asset.deviceId,
          asset.assetType,
          asset.currentPosition!!,
          asset.numberOfPositionUpdates
      )
    }
  }
}
