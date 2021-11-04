import { randomUUID as uuid } from 'crypto'
import { IVisitor } from 'domain/utils/Visitor.interface'
import { InvalidStateError } from './errors/InvalidStateError'
import { Service } from './Service'

export class System {
  static create(name: string, id: string | undefined = undefined): System {
    if (!name) throw new InvalidStateError('System name cannot be blank or undefined')
    // @ts-ignore
    const isString = typeof name === 'string' || name instanceof String
    if (!isString) throw new InvalidStateError('System name must be a string')
    return new System(name, id)
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

  public accept(visitor: IVisitor): void {
    visitor.visitSystem(this)
  }
}
