package com.hookiesolutions.cat.subscription

import com.hookiesolutions.cat.subscription.domain.PositionUpdate
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import java.time.LocalDateTime

@SpringBootTest
class PrototypeApplicationTests {

  @Autowired
  lateinit var c: MappingMongoConverter

  @Test
  fun contextLoads() {
  }

  @Test
  internal fun name() {
    val currentPosition = PositionUpdate(GeoJsonPoint(1.0, 1.0), LocalDateTime.now(), mapOf())

    val currentPositionList = listOf(currentPosition)

    val entityType: Class<*> = currentPositionList.javaClass

    val a = MutableCollection::class.java.isAssignableFrom(entityType)
    println(a)

    val cp = Document()
    c.write(currentPosition, cp)
    val toJson = cp.toJson()
    val d = Document.parse("""{
            ${'$'}concatArrays: [
                "${'$'}positions", [ $toJson ]
            ]
          }      
    """.trimIndent())

    println(d)
  }

  @Test
  internal fun locations() {
/*
    val position = GeoJsonPoint(-106.0, -40.0)
    val enterpriseId = "5ef36031cb509c53a57dd1c7"
    enterpriseRepository.locationsFor(position, enterpriseId)
        .test()
        .assertNext {
          assertThat(it.name).isEqualTo("Location 4")
        }
        .verifyComplete()
*/
  }
}
