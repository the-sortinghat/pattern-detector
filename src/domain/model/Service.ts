import { randomUUID as uuid } from 'crypto'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
import { Operation } from './Operation'
import { IVisitor } from 'domain/utils/Visitor.interface'
import { InvalidStateError } from './errors/InvalidStateError'

export class Service {
  static create(name: string, id: string | undefined = undefined): Service {
    if (!name) throw new InvalidStateError('service name cannot be blank or undefined')
    // @ts-ignore
    const isString = typeof name === 'string' || name instanceof String
    if (!isString) throw new InvalidStateError('service name must be a string')
    return new Service(name, id)
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

  public accept(visitor: IVisitor): void {
    visitor.visitService(this)
  }
}
