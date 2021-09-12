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
