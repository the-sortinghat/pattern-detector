import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { Service } from '../../../domain/model/Service'
import { Operation } from '../../../domain/model/Operation'

export class ServiceDAO implements IServiceDAO {
  constructor(private readonly operationDao: IOperationDAO) {}

  public docToService(doc: any): Service {
    const svc = Service.create(doc.name, doc.uuid)

    if (doc.operations?.length > 0)
      doc.operations
        .map((op: any): Operation => this.operationDao.docToOperation(op))
        .forEach((op: Operation) => svc.addOperation(op))

    return svc
  }

  public serviceToDoc(service: Service): any {
    return {
      name: service.name,
      uuid: service.id,
      operations: service.operations.map((op: Operation): any =>
        this.operationDao.operationToDoc(op),
      ),
    }
  }
}
