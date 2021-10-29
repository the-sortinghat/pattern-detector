import { Database } from './Database'
import { Service } from './Service'

export class DatabaseUsage {
  static create(from: Service, of: Database): DatabaseUsage {
    return new DatabaseUsage(from, of)
  }

  private constructor(public readonly fromService: Service, public readonly ofDatabase: Database) {
    fromService.addUsage(this)
    ofDatabase.addUsage(this)
  }
}
