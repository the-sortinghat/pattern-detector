import { Database } from '../../domain/model/Database'

export interface IDatabaseDAO {
  store: (database: Database) => Promise<Database>
  findOne: (id: string) => Promise<Database>
}