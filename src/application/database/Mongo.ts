import { Db, MongoClient } from 'mongodb'

import { Logger } from '../logger/Logger'

export async function setupDB(dbName: string, logger: Logger): Promise<Db> {
  const url = process.env.MONGO_URL as string
  const client = new MongoClient(url)
  await client.connect()
  logger.info('Connected to MongoDB successfully!')

  return client.db(dbName)
}
