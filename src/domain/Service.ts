import { randomUUID as uuid } from 'crypto'
import { DatabaseUsage } from './DatabaseUsage'
import { Operation } from './Operation'

export class Service {
  static create(name: string): Service {
    return new Service(name)
  }

  private ops: Operation[]
  private _usages: DatabaseUsage[]

  private constructor(public readonly name: string, public readonly id = uuid()) {
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
}
