import { Document } from 'mongodb'

import { Service } from '../../domain/model/Service'
import { System } from '../../domain/model/System'
import { Operation } from '../../domain/model/Operation'

export interface IScopedOperation {
  operation: Operation
  service: Service
  system: System
}

export interface IOperationDAO {
  store: (operation: Operation, serviceID: string) => Promise<void>
  findOne: (id: string) => Promise<IScopedOperation>
  docToOperation: (doc: Document) => Operation
}
