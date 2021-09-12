package com.hookiesolutions.cat.subscription.common.model

import org.springframework.data.annotation.Id

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:30
 */
abstract class AbstractEntity : AbstractDocument() {
  @Id
  var id: String? = null
}
