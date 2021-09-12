package com.hookiesolutions.cat.subscription.config.mongo

import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import com.hookiesolutions.cat.subscription.service.TimeMachine
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 13:50
 */
@Component
class EntityAuditorAware<T: AbstractEntity>(
    private val timeMachine: TimeMachine,
    private val log: Logger
): ReactiveBeforeConvertCallback<T> {
  override fun onBeforeConvert(entity: T, collection: String): Publisher<T> {
    log.info("adding audit data to the document: '{}'", entity.javaClass.name)
    return ReactiveSecurityContextHolder.getContext()
        .map{ it.authentication }
        .filter { it != null }
        .filter {  it.isAuthenticated }
        .map(Authentication::getPrincipal)
        .cast(User::class.java)
        .map {
          entity.createdDate = timeMachine.now()
          entity.createdBy = it.username
          entity.lastModifiedBy = it.username
          entity.auditAction = "Create"
          return@map entity
        }
        .switchIfEmpty(Mono.just(entity))
  }
}
