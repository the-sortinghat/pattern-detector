import { randomUUID as uuid } from 'crypto'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
import { MetricsCollector } from '../metrics/MetricsCollector'

export class Database {
  static create(make: string): Database {
    return new Database(make)
  }

  private _usages: DatabaseUsage[]
  public readonly measuresVessel = new MeasuresVessel()

  private constructor(public readonly make: string, public readonly id = uuid()) {
    this._usages = []
  }

  public addUsage(usage: DatabaseUsage): void {
    this._usages.push(usage)
  }

  public get usages(): DatabaseUsage[] {
    return Object.assign([], this._usages)
  }

  public accept(collector: MetricsCollector): void {}
}
