import { Document } from 'mongodb'

import { Service } from '../../domain/model/Service'
import { System } from '../../domain/model/System'

export interface IScopedService {
  system: System
  service: Service
}

export interface IServiceDAO {
  findOne: (serviceID: string) => Promise<IScopedService>
  docToService: (doc: Document) => Service
  serviceToDoc: (service: Service) => any
}
