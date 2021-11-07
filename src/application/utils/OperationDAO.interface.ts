import { Document } from 'mongodb'

import { Operation } from '../../domain/model/Operation'

export interface IOperationDAO {
  docToOperation: (doc: Document) => Operation
  operationToDoc: (operation: Operation) => any
}
