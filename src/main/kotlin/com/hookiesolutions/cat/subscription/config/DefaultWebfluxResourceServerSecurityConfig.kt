package com.hookiesolutions.cat.subscription.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class DefaultWebfluxResourceServerSecurityConfig {
  @Bean
  internal fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    val openApiUris = arrayOf(
        "/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html", "/webjars/swagger-ui/**"
    )
    http.csrf().disable()
    http.authorizeExchange {
      it.pathMatchers(*openApiUris).permitAll()
      it.pathMatchers("/actuator/**").permitAll()
      it.pathMatchers("/demo/**").permitAll()
      it.pathMatchers(HttpMethod.OPTIONS,"/**").permitAll()
      it.pathMatchers(HttpMethod.GET,"/generateId").permitAll()
      it.pathMatchers(HttpMethod.POST,"/subscription/handleEvent").permitAll()
      it.pathMatchers(HttpMethod.POST,"/position").permitAll()
      it.anyExchange().authenticated()
    }
    http.httpBasic(Customizer.withDefaults())
    http.formLogin(Customizer.withDefaults())

    return http.build()
  }
}
