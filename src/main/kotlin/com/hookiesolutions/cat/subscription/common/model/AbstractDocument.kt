package com.hookiesolutions.cat.subscription.common.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:29
 */
abstract class AbstractDocument {
  @CreatedDate
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonIgnore
  var createdDate: LocalDateTime? = null

  @LastModifiedDate
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonIgnore
  var lastModifiedDate: LocalDateTime? = null

  @CreatedBy
  @JsonIgnore
  var createdBy: String? = null

  @LastModifiedBy
  @JsonIgnore
  var lastModifiedBy: String? = null

  @Version
  @JsonIgnore
  var version: Long? = null

  @JsonIgnore
  var auditAction: String? = null
}
