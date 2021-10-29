import { randomUUID as uuid } from 'crypto'

export class Database {
  static create(make: string): Database {
    return new Database(make)
  }

  private constructor(public readonly make: string, public readonly id = uuid()) {}
}
