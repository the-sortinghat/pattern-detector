import { IVisitor } from '../utils/Visitor.interface'

export enum HTTPVerb {
  GET,
  PUT,
  POST,
  PATCH,
  DELETE,
}

export class Operation {
  static create(verb: HTTPVerb, path: string): Operation {
    return new Operation(verb, path)
  }

  private constructor(public readonly verb: HTTPVerb, public readonly path: string) {}

  public accept(visitor: IVisitor): void {}
}
