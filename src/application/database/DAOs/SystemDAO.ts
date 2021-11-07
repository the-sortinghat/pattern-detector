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

  public async store(system: System): Promise<void> {
    const sysDoc = this.systemToDoc(system)
    await this.systemCollection.updateOne({ uuid: system.id }, sysDoc, { upsert: true })
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

  public systemToDoc(system: System): any {
    return {
      name: system.name,
      uuid: system.id,
      services: system.services.map((svc: Service): any => this.svcDao.serviceToDoc(svc)),
    }
  }
}
