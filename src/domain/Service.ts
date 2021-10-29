import { randomUUID as uuid } from 'crypto'
import { Operation } from './Operation'

export class Service {
  static create(name: string): Service {
    return new Service(name)
  }

  private ops: Operation[]

  private constructor(public readonly name: string, public readonly id = uuid()) {
    this.ops = []
  }

  public addOperation(operation: Operation): void {
    this.ops.push(operation)
  }

  public get operations(): Operation[] {
    return Object.assign([], this.ops)
  }
}
