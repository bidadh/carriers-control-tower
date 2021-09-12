package com.hookiesolutions.cat.subscription.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/6/20 02:32
 */
@Component
class SecurityHandler {
  fun loggedInUser(): Mono<User> {
    return ReactiveSecurityContextHolder.getContext()
        .map { it.authentication }
        .filter { it != null }
        .filter { it.isAuthenticated }
        .map(Authentication::getPrincipal)
        .cast(User::class.java)
  }

  fun loggedInUsername(): Mono<String> {
    return loggedInUser()
      .map { it.username }
      .switchIfEmpty { "cat".toMono() }
      .onErrorReturn("cat")
  }
}
