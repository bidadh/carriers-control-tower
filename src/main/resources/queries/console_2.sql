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
    $count:
  }
])

db.event.aggregate([ { $group: {"_id":"$asset.deviceId" , "number":{$sum:1}} } ])

db.event.distinct()