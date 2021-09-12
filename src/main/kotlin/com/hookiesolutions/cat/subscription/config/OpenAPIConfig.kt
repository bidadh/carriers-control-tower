package com.hookiesolutions.cat.subscription.config

import com.hookiesolutions.cat.subscription.web.AssetController
import com.hookiesolutions.cat.subscription.web.AssetTypeController
import com.hookiesolutions.cat.subscription.web.EnterpriseController
import com.hookiesolutions.cat.subscription.web.EventController
import com.hookiesolutions.cat.subscription.web.PositionController
import com.hookiesolutions.cat.subscription.web.SSEController
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 19/6/20 17:55
 */
@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Ericsson Critical Asset Tracking API",
        version = "1.0.0"
    )
)
@SecurityScheme(
    name = OpenAPIConfig.BASIC_SCHEME,
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
class OpenAPIConfig {
  @Bean
  fun assetOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${AssetController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("asset").pathsToMatch(*paths)
        .build()
  }

  @Bean
  fun assetTypeOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${AssetTypeController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("assetType").pathsToMatch(*paths)
        .build()
  }

  @Bean
  fun enterpriseOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${EnterpriseController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("enterprise").pathsToMatch(*paths)
        .build()
  }

  @Bean
  fun locationOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${PositionController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("position").pathsToMatch(*paths)
        .build()
  }

  @Bean
  fun sseOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${SSEController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("sse").pathsToMatch(*paths)
        .build()
  }

  @Bean
  fun eventOpenApi(): GroupedOpenApi {
    val paths = arrayOf("${EventController.REQUEST_MAPPING}/**")
    return GroupedOpenApi.builder().group("event").pathsToMatch(*paths)
        .build()
  }

  companion object {
    const val BASIC_SCHEME = "basicScheme"
  }
}
