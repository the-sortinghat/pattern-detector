import { IVisitor } from '../utils/Visitor.interface'
import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { Operation } from '../model/Operation'
import { Service } from '../model/Service'
import { System } from '../model/System'
import { Metrics } from './MeasuresVessel'

export class MetricsCollector implements IVisitor {
  static create(): MetricsCollector {
    return new MetricsCollector()
  }

  protected constructor() {}

  visitSystem(system: System): void {
    system.services.forEach((svc: Service) => svc.accept(this))
  }

  visitService(service: Service): void {
    service.operations.forEach((op: Operation) => {
      service.measuresVessel.increment(Metrics.nOperations)
      op.accept(this)
    })
    service.usages.forEach((dbUsage: DatabaseUsage) => {
      service.measuresVessel.increment(Metrics.nDatabaseUsing)
      dbUsage.accept(this)
    })
  }

  visitDatabaseUsage(usage: DatabaseUsage): void {
    usage.ofDatabase.accept(this)
  }

  visitDatabase(database: Database): void {
    database.usages.forEach(() => database.measuresVessel.increment(Metrics.nUsageClients))
  }

  visitOperation(operation: Operation): void {}
}
