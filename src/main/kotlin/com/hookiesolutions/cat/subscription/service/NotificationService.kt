package com.hookiesolutions.cat.subscription.service

import com.hookiesolutions.cat.subscription.domain.Asset
import org.slf4j.Logger
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:23
 */
@Service
class NotificationService(
    private val notificationChannel: MessageChannel,
    private val log: Logger
) {
  fun notifyCreateAsset(asset: Asset) {
    log.info("Notifying about new asset: '{}'", asset.name)
    val message = MessageBuilder.withPayload(asset)
        .build()
    notificationChannel.send(message)
  }
}
