import { Collection, Db } from 'mongodb'

import { Database } from '../../../domain'

import { IDatabaseDAO } from '../../utils/DatabaseDAO.interface'

export class DatabaseDAO implements IDatabaseDAO {
  private readonly databaseCollection: Collection

  constructor(db: Db) {
    this.databaseCollection = db.collection('databases')
  }

  public docToDatabase(doc: any): Database {
    return Database.create(doc.make, doc.uuid)
  }

  public databaseToDoc(db: Database): any {
    return {
      make: db.make,
      uuid: db.id,
    }
  }

  public async store(db: Database): Promise<void> {
    await this.databaseCollection.updateOne(
      { uuid: db.id },
      { $set: this.databaseToDoc(db) },
      {
        upsert: true,
      },
    )
  }

  public async findOne(id: string): Promise<Database> {
    const res = await this.databaseCollection.findOne({ uuid: id })

    if (!res) return Promise.reject(`not found - Database ${id}`)

    return this.docToDatabase(res)
  }
}
