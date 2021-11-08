import { Operation, HTTPVerb } from '../../../domain'

import { IOperationDAO } from '../../utils/OperationDAO.interface'

export class OperationDAO implements IOperationDAO {
  public docToOperation(doc: any): Operation {
    const verb = OperationDAO.verbStringToHTTPVerb(doc.verb)

    return Operation.create(verb, doc.path)
  }

  public operationToDoc(operation: Operation): any {
    return {
      verb: operation.verb,
      path: operation.path,
    }
  }

  public static verbStringToHTTPVerb(verb: string): HTTPVerb {
    const map: { [key: string]: HTTPVerb } = {
      GET: HTTPVerb.GET,
      POST: HTTPVerb.POST,
      PUT: HTTPVerb.PUT,
      PATCH: HTTPVerb.PATCH,
      DELETE: HTTPVerb.DELETE,
    }

    const verbUpper = verb.toUpperCase()

    return map[verbUpper] || HTTPVerb.GET
  }
}
