db.asset.updateMany({},{
  $unset: {
    lastPosition: 1,
    currentPosition: 1
  },
  $set: {
    auditAction: "Create",
    positions: [],
    currentLocations: [],
    numberOfPositionUpdates: 0
  }
});

db.enterprise.updateMany({}, {
  $set: {
    "locations.$[].numberOfAssets": 0
  },
  $unset: {
    "locations.$[].eventUpdate": 1
  }
});



db.location.remove({});
db.event.remove({});


db.asset.find({deviceId: "5ef5416adf8f49435dc4300a"}, {lastPosition: 1, currentPosition: 1, positions: 1});
db.asset.find({}, {lastPosition: 1, currentPosition: 1, positions: 1});

db.asset.updateOne(
    {deviceId: "5ef379e08ef29a147193a238"},
    [
      {
        $set: {
          numberOfPositionUpdates: 1,
          lastPosition: "$currentPosition",
          currentPosition: {
            "position": {
              "type": "Point",
              "coordinates": [-100, -37]
            },
            "properties": {
              "status": "New Status"
            }
          },
          positions: {
            $concatArrays: [
              "$positions", [ {
                "position": {
                  "type": "Point",
                  "coordinates": [-100, -37]
                },
                "properties": {
                  "status": "New Status"
                }
              }]
            ]
          }
        }
      }
    ]
)

db.enterprise.updateOne(
    {locations: {$elemMatch: {name: "Warehouse"}}},
    {
      $set: {
        "locations.$.test": 1
      }
    }
)









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













db.asset.aggregate([
  {
    $project: {
      currentLocations: 1
    }
  },
  {
    $unwind: {path: "$currentLocations"}
  },
  {
    $group: {
      _id: "currentLocations",
      count: {$sum: 1}
    }
  },
  {
    $project: {
      name: "$_id",
      count: 1,
      _id: 0

    }
  }
])

db.event.aggregate([
  {
    $count: "count"
  }
])

db.event.aggregate([ { $group: {"_id":"$asset.deviceId" , "number":{$sum:1}} } ])

db.event.distinct()