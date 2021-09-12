package com.hookiesolutions.cat.subscription.service

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 13:58
 */
@Service
class TimeMachine {
  fun clock(): Clock = Clock.systemUTC()

  fun now(): LocalDateTime = LocalDateTime.now(clock())
}
