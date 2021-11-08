import { IDatabaseDAO } from '../../utils/DatabaseDAO.interface'
import { DatabaseUsage } from '../../../domain/model/DatabaseUsage'
import { Service } from '../../../domain/model/Service'
import { IDatabaseUsageDAO } from '../../utils/DatabaseUsageDAO.interface'

export class DatabaseUsageDAO implements IDatabaseUsageDAO {
  constructor(private readonly dbDao: IDatabaseDAO) {}

  public async docToDatabaseUsage(doc: string, service: Service): Promise<DatabaseUsage> {
    const dbID = doc

    const db = await this.dbDao.findOne(dbID)

    return DatabaseUsage.create(service, db)
  }

  public databaseUsageToDoc(usage: DatabaseUsage): any {
    return usage.ofDatabase.id
  }
}
