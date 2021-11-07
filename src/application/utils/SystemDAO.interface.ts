import { Document } from 'mongodb'

import { System } from '../../domain/model/System'

export interface ISystemDAO {
  store: (system: System) => Promise<void>
  findOne: (sID: string) => Promise<System>
  docToSystem: (doc: Document) => System
  systemToDoc: (system: System) => any
}
