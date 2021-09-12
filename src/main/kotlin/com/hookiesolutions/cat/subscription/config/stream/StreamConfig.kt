package com.hookiesolutions.cat.subscription.config.stream

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.MessageChannels
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:13
 */
@Configuration
class StreamConfig {
  @Bean
  fun devicePositionsOutputChannel(): MessageChannel {
    return MessageChannels.publishSubscribe().get()
  }

  @Bean
  fun devicePositionsInputChannel(): MessageChannel {
    return MessageChannels.publishSubscribe().get()
  }

  @Bean
  fun sseChannel(): SubscribableChannel {
    return MessageChannels.publishSubscribe().get()
  }

  @Bean
  fun notificationChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun locationDefinitionDispatcherChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun enteredLocationChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun exitedLocationChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun locationEventSubscriptionsChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun postToSubscribersChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun locationsSseChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun logChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun movingAssetsChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun positionUpdateCountChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun eventCountChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun locationLastActiveTimeChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun assetWithEventsCountChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun movingAssetsLocationHandlerChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }

  @Bean
  fun saveEventChannel(): MessageChannel {
    return MessageChannels.direct().get()
  }
}
