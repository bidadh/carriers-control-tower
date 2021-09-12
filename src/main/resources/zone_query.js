db.enterprise.find({
  "zones.polygon" : {
    "$geoIntersects" : {
      "$geometry" : {
        "type" : "Point" ,
        "coordinates" : [ -106, -40]
      }
    }
  }
});

db.enterprise.aggregate([
  {
    $match: {
      "zones.polygon" : {
        "$geoIntersects" : {
          "$geometry" : {
            "type" : "Point" ,
            "coordinates" : [ -106, -40]
          }
        }
      }
    }
  },
  {
    $unwind: { path: "$zones"}
  },
  {
    $match: {
      "zones.polygon" : {
        "$geoIntersects" : {
          "$geometry" : {
            "type" : "Point" ,
            "coordinates" : [ -106, -40]
          }
        }
      }
    }
  }
]);
