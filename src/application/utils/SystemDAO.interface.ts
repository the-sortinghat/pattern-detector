import { Document } from 'mongodb'

import { Service } from '../../domain/model/Service'
import { System } from '../../domain/model/System'

export interface IScopedService {
  service: Service
  parentSystem: System
}

export interface ISystemDAO {
  store: (system: System) => Promise<void>
  findOne: (sID: string) => Promise<System>
  findOneService: (svcID: string) => Promise<IScopedService>
  docToSystem: (doc: Document) => System
  systemToDoc: (system: System) => any
}
