import { Document } from 'mongodb'

import { Service } from '../../domain/model/Service'

export interface IServiceDAO {
  docToService: (doc: Document) => Promise<Service>
  serviceToDoc: (service: Service) => any
}
