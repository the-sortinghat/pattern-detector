import { randomUUID as uuid } from 'crypto'

export class Service {
  static create(name: string): Service {
    return new Service(name)
  }

  private constructor(public readonly name: string, public readonly id = uuid()) {}
}
