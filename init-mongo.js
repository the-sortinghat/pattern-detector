db = db.getSiblingDB(process.env.USVISION_DATABASE_NAME);

db.createUser(
  {
    user: process.env.USVISION_DATABASE_USERNAME,
    pwd: process.env.USVISION_DATABASE_PASSWORD,
    roles: [
      {
        role: "readWrite",
        db: process.env.USVISION_DATABASE_NAME
      }
    ]
  }
);
