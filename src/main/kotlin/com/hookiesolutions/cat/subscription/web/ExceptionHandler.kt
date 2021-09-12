package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.common.exception.DocumentNotFoundException
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.UncategorizedMongoDbException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 21/6/20 17:31
 */
@RestControllerAdvice
class ExceptionHandler {
  @ExceptionHandler(UncategorizedMongoDbException::class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handleDBException(ex: UncategorizedMongoDbException): Map<String,Any> {
    val causeMessage = ex.cause?.localizedMessage?: ""
    return mapOf(
        "message" to ex.localizedMessage,
        "cause" to causeMessage
    )
  }

  @ExceptionHandler(DuplicateKeyException::class)
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  fun handleDuplicateKeyException(ex: DuplicateKeyException): Map<String,Any> {
    val causeMessage = ex.cause?.localizedMessage?: ""
    return mapOf(
        "message" to ex.localizedMessage,
        "cause" to causeMessage
    )
  }

  @ExceptionHandler(DocumentNotFoundException::class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handleDocumentNotFoundException(ex: DocumentNotFoundException): Map<String,Any> {
    return mapOf(
        "message" to ex.localizedMessage
    )
  }
}
