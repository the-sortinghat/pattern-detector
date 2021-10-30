import { randomUUID as uuid } from 'crypto'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
import { Operation } from './Operation'
import { MetricsCollector } from '../metrics/MetricsCollector'

export class Service {
  static create(name: string): Service {
    return new Service(name)
  }

  protected ops: Operation[]
  protected _usages: DatabaseUsage[]
  public readonly measuresVessel = new MeasuresVessel()

  protected constructor(public readonly name: string, public readonly id = uuid()) {
    this.ops = []
    this._usages = []
  }

  public addOperation(operation: Operation): void {
    this.ops.push(operation)
  }

  public get operations(): Operation[] {
    return Object.assign([], this.ops)
  }

  public addUsage(usage: DatabaseUsage): void {
    this._usages.push(usage)
  }

  public get usages(): DatabaseUsage[] {
    return Object.assign([], this._usages)
  }

  public accept(collector: MetricsCollector): void {}
}
