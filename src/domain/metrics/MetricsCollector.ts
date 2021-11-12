import { IVisitor } from '../utils/Visitor.interface'
import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { Operation } from '../model/Operation'
import { Service } from '../model/Service'
import { System } from '../model/System'
import { MeasuresVessel, Metrics } from './MeasuresVessel'

export interface IObjectVessels {
  [id: string]: MeasuresVessel
}

export class MetricsCollector implements IVisitor {
  static create(): MetricsCollector {
    return new MetricsCollector()
  }

  private readonly vessels: IObjectVessels

  protected constructor() {
    this.vessels = {}
  }

  visitSystem(system: System): void {
    system.services.forEach((svc: Service) => svc.accept(this))
  }

  visitService(service: Service): void {
    const id = service.id

    if (!this.vessels[id]) this.vessels[id] = new MeasuresVessel()

    service.operations.forEach((op: Operation) => {
      this.vessels[id].increment(Metrics.nOperations)
      op.accept(this)
    })
    service.usages.forEach((dbUsage: DatabaseUsage) => {
      this.vessels[id].increment(Metrics.nDatabaseUsing)
      dbUsage.accept(this)
    })
  }

  visitDatabaseUsage(usage: DatabaseUsage): void {
    usage.ofDatabase.accept(this)
  }

  visitDatabase(database: Database): void {
    const id = database.id

    if (!this.vessels[id]) this.vessels[id] = new MeasuresVessel()

    database.usages.forEach(() => this.vessels[id].increment(Metrics.nUsageClients))
  }

  visitOperation(operation: Operation): void {}

  public nUsageClients(id: string): number {
    return this.vessels[id]?.nUsageClients || 0
  }

  public nOperations(id: string): number {
    return this.vessels[id]?.nOperations || 0
  }

  public nDatabaseUsing(id: string): number {
    return this.vessels[id]?.nDatabaseUsing || 0
  }

  public get metrics(): IObjectVessels {
    return this.vessels
  }
}
