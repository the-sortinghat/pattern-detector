import { IVisitor } from '../utils/Visitor.interface'
import { InvalidStateError } from './errors/InvalidStateError'

export enum HTTPVerb {
  GET = 'GET',
  PUT = 'PUT',
  POST = 'POST',
  PATCH = 'PATCH',
  DELETE = 'DELETE',
}

export class Operation {
  static create(verb: HTTPVerb, path: string): Operation {
    const verbs = [HTTPVerb.DELETE, HTTPVerb.PATCH, HTTPVerb.POST, HTTPVerb.PUT, HTTPVerb.GET]
    const validVerb = verbs.includes(verb)
    if (!validVerb) throw new InvalidStateError(`http verb must be one of: ${verbs.join(', ')}`)

    const rgx = /^(\/[A-Za-z0-9\-_]+)+$/
    const validPath = rgx.test(path)

    if (!validPath) throw new InvalidStateError('path must be of format /foo/baz/123, got ' + path)

    return new Operation(verb, path)
  }

  private constructor(public readonly verb: HTTPVerb, public readonly path: string) {}

  public accept(visitor: IVisitor): void {}
}
