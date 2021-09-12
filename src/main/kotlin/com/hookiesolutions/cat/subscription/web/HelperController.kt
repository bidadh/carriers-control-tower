package com.hookiesolutions.cat.subscription.web

import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/6/20 01:56
 */
@RestController
class HelperController {
  @GetMapping("/generateId", produces = [MediaType.TEXT_PLAIN_VALUE])
  fun createObjectId(): String = ObjectId.get().toHexString()
}
