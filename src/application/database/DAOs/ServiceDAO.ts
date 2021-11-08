import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { Service } from '../../../domain/model/Service'
import { Operation } from '../../../domain/model/Operation'
import { IDatabaseUsageDAO } from '../../utils/DatabaseUsageDAO.interface'
import { DatabaseUsage } from '../../../domain/model/DatabaseUsage'
import { IDatabaseDAO } from '../../utils/DatabaseDAO.interface'

export class ServiceDAO implements IServiceDAO {
  constructor(
    private readonly operationDao: IOperationDAO,
    private readonly usageDao: IDatabaseUsageDAO,
    private readonly dbDao: IDatabaseDAO,
  ) {}

  public async docToService(doc: any): Promise<Service> {
    const svc = Service.create(doc.name, doc.uuid)

    if (doc.operations?.length > 0)
      doc.operations
        .map((op: any): Operation => this.operationDao.docToOperation(op))
        .forEach((op: Operation) => svc.addOperation(op))

    if (doc.databaseUsages?.length > 0) {
      for (const dbID of doc.databaseUsages) {
        const of = await this.dbDao.findOne(dbID)
        DatabaseUsage.create(svc, of)
      }
    }

    return svc
  }

  public serviceToDoc(service: Service): any {
    return {
      name: service.name,
      uuid: service.id,
      operations: service.operations.map((op: Operation): any =>
        this.operationDao.operationToDoc(op),
      ),
      databaseUsages: service.usages.map((usage: DatabaseUsage): any =>
        this.usageDao.databaseUsageToDoc(usage),
      ),
    }
  }
}
