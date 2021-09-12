package com.hookiesolutions.cat.subscription.config.stream

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.MessageChannels
import org.springframework.messaging.MessageChannel

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 23/6/20 14:43
 */
@Configuration
class Bindings {
  @Bean
  fun assetsOutputChannel(): MessageChannel {
    return MessageChannels.publishSubscribe().get()
  }

  @Bean
  fun enteredLocationsOutputChannel(): MessageChannel {
    return MessageChannels.publishSubscribe().get()
  }

  @Bean
  fun exitedLocationsOutputChannel(): MessageChannel {
    return MessageChannels.publishSubscribe().get()
  }

  companion object {
    interface Channels {
      companion object {
        interface Input {
          companion object {
            const val DEVICE_POSITIONS_INPUT_CHANNEL = "devicePositionsInputChannel"
          }
        }
        interface Output {
          companion object {
            const val DEVICE_POSITIONS_OUTPUT_CHANNEL = "devicePositionsOutputChannel"
          }
        }
      }
    }
  }
}
