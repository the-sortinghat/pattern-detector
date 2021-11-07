import { Document } from 'mongodb'

import { Service } from '../../domain/model/Service'

export interface IServiceDAO {
  docToService: (doc: Document) => Service
  serviceToDoc: (service: Service) => any
}
