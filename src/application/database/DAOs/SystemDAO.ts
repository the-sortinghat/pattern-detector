import { Collection, Db } from 'mongodb'

import { Service, System } from '../../../domain'

import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { IScopedService, ISystemDAO } from '../../utils/SystemDAO.interface'

export class SystemDAO implements ISystemDAO {
  private readonly systemCollection: Collection

  constructor(db: Db, private readonly svcDao: IServiceDAO) {
    this.systemCollection = db.collection('systems')
  }

  public async store(system: System): Promise<void> {
    const sysDoc = this.systemToDoc(system)
    await this.systemCollection.updateOne({ uuid: system.id }, { $set: sysDoc }, { upsert: true })
  }

  public async findOne(id: string): Promise<System> {
    const result = await this.systemCollection.findOne({ uuid: id })

    if (!result) return Promise.reject(`not found - System ${id}`)

    return this.docToSystem(result)
  }

  public async findOneService(svcID: string): Promise<IScopedService> {
    const result = await this.systemCollection.findOne({
      services: { $elemMatch: { uuid: svcID } },
    })

    if (!result) return Promise.reject(`not found - System ${svcID}`)

    const system = await this.docToSystem(result)

    const service = system.services.find((svc: Service) => svc.id === svcID) as Service

    return { parentSystem: system, service }
  }

  public async docToSystem(doc: any): Promise<System> {
    const sys = System.create(doc.name, doc.uuid)

    for (const docSvc of doc.services) {
      const svc = await this.svcDao.docToService(docSvc)
      sys.addService(svc)
    }

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
