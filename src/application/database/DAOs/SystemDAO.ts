import { Collection, Db } from 'mongodb'

import { Service } from '../../../domain/model/Service'
import { System } from '../../../domain/model/System'
import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { ISystemDAO } from '../../utils/SystemDAO.interface'

export class SystemDAO implements ISystemDAO {
  private readonly systemCollection: Collection

  constructor(db: Db, private readonly svcDao: IServiceDAO) {
    this.systemCollection = db.collection('systems')
  }

  store(system: System): Promise<void> {
    throw new Error('not implemented')
  }

  public async findOne(id: string): Promise<System> {
    const result = await this.systemCollection.findOne({ uuid: id })

    if (!result) return Promise.reject(`not found - System ${id}`)

    return this.docToSystem(result)
  }

  public docToSystem(doc: any): System {
    const sys = System.create(doc.name, doc.uuid)

    doc.services
      .map((docSvc: any): Service => this.svcDao.docToService(docSvc))
      .forEach((svc: Service) => sys.addService(svc))

    return sys
  }
}
