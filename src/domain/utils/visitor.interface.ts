import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { Operation } from '../model/Operation'
import { Service } from '../model/Service'
import { System } from '../model/System'

export interface IVisitor {
  visitSystem: (target: System) => void
  visitService: (target: Service) => void
  visitOperation: (target: Operation) => void
  visitDatabaseUsage: (target: DatabaseUsage) => void
  visitDatabase: (target: Database) => void
}
