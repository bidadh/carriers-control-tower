function updateEvent(e) {
  let c = e.locations[0].criticality
  db.event.updateOne(
      {"_id": ObjectId(e._id)},
      {
        $set: {
          "criticality": c
        }
      }
  );
}

let d = db.event.find();

d.forEach(updateEvent);