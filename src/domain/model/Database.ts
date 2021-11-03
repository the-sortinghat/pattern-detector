import { InvalidStateError } from './errors/InvalidStateError'
import { randomUUID as uuid } from 'crypto'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
import { IVisitor } from 'domain/utils/Visitor.interface'

export class Database {
  static create(make: string): Database {
    if (!make) throw new InvalidStateError('service make cannot be blank or undefined')
    // @ts-ignore
    const isString = typeof make === 'string' || make instanceof String
    if (!isString) throw new InvalidStateError('service make must be a string')

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

  public accept(visitor: IVisitor): void {
    visitor.visitDatabase(this)
  }
}
