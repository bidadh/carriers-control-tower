db.enterprise.find({
  "zones.polygon": {
    "$geoIntersects": {
      "$geometry": {
        "type": "Point",
        "coordinates": [-106, -40]
      }
    }
  }
});

db.enterprise.aggregate([
  {
    $match: {
      _id: ObjectId("5ef36031cb509c53a57dd1c7"),
      "zones.polygon": {
        "$geoIntersects": {
          "$geometry": {
            "type": "Point",
            "coordinates": [-106, -40]
          }
        }
      }
    }
  },
  {$project: {zones: 1}},
  {$unwind: {path: "$zones"}},
  {
    $match: {
      "zones.polygon": {
        "$geoIntersects": {
          "$geometry": {
            "type": "Point",
            "coordinates": [-106, -40]
          }
        }
      }
    }
  },
  {$replaceRoot: {newRoot: "$zones"}}
]);


db.enterprise.aggregate(
    [{
      "$match": {
        "_id": {"$oid": "5ef36031cb509c53a57dd1c7"},
        "zones.polygon": {"$geoIntersects": {"$geometry": {"type": "Point", "coordinates": [-106, -40]}}}
      }
    }]
    )