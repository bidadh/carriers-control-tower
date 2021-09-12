db.enterprise.update(
    {locations: {$elemMatch: { "name": "Ericsson Studio"}}},
    {
      $set: {
        "locations.$.polygon": {
          type: "MultiPoint",
          coordinates: [[
            [17.9529, 59.4043],
            [17.9535, 59.4038],
            [17.9543, 59.4041],
            [17.9538, 59.4045],
            [17.9529, 59.4043]
          ]]
        }
      }
    }
);


db.event.updateMany(
    {
      locations: {
        $elemMatch: {
          name: "Tele2 AB"
        }
      }
    },
    {
      $set: {
        "locations.$.name": "CSP"
      }
    }
)
