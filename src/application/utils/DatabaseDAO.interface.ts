import { Database } from '../../domain/model/Database'

export interface IDatabaseDAO {
  store: (database: Database) => Promise<void>
  findOne: (id: string) => Promise<Database>
  docToDatabase: (doc: any) => Database
  databaseToDoc: (database: Database) => any
}
