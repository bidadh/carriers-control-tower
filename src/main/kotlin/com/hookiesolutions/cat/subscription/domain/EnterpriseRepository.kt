package com.hookiesolutions.cat.subscription.domain

import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface EnterpriseRepository: ReactiveMongoRepository<Enterprise, String> {
  @Aggregation(
      pipeline = [
        "  {\n" +
            "    ${'$'}match: {\n" +
            "      _id: ObjectId('?1'),\n" +
            "      \"locations.polygon\" : {\n" +
            "        \"${'$'}geoIntersects\" : {\n" +
            "          \"${'$'}geometry\" : ?0" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n",
        "  { ${'$'}project: {locations: 1}}\n",
        "  { ${'$'}unwind: { path: \"${'$'}locations\"}}\n",
        "  {\n" +
            "    ${'$'}match: {\n" +
            "      \"locations.polygon\" : {\n" +
            "        \"${'$'}geoIntersects\" : {\n" +
            "          \"${'$'}geometry\" : ?0" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }",
        "{ ${'$'}replaceRoot: { newRoot: \"${'$'}locations\" } }"
      ]
  )
  fun locationsFor(position: GeoJsonPoint, enterpriseId: String): Flux<Geofence>
}
