import { randomUUID as uuid } from 'crypto'
import { MetricsCollector } from '../metrics/MetricsCollector'
import { Service } from './Service'

export class System {
  static create(name: string): System {
    return new System(name)
  }

  private _services: Service[]

  private constructor(public readonly name: string, public readonly id = uuid()) {
    this._services = []
  }

  public addService(service: Service): void {
    this._services.push(service)
  }

  public get services(): Service[] {
    return Object.assign([], this._services)
  }

  public accept(collector: MetricsCollector): void {
    collector.collectFromSystem(this)
  }
}
