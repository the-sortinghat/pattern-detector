import { Db, MongoClient } from 'mongodb'

export async function setupDB(dbName = 'detector'): Promise<Db> {
  const url = process.env.MONGO_URL as string
  const client = new MongoClient(url)
  await client.connect()
  console.log('Connected to MongoDB successfully!')

  return client.db(dbName)
}
