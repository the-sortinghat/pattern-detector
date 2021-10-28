import { randomUUID as uuid } from 'crypto'

export class System {
  static create(name: string): System {
    return new System(name)
  }

  private constructor(public readonly name: string, public readonly id = uuid()) {}
}
