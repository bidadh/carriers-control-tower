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

db.enterprise.update(
    {
      "name": "Sandvik"
    },
    {
      $set: {
        "locations.$[elem].criticality": "Normal"
      }
    },
    {
      multi: true,
      arrayFilters: [
        { "elem.name": { $regex: /Entrance.*/}}
      ]
    }
);

db.enterprise.find(
    {
    },
    {
      "locations.name": 1,
      "locations.criticality": 1
    }
  );