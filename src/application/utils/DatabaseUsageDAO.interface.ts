import { DatabaseUsage } from '../../domain/model/DatabaseUsage'
import { Service } from '../../domain/model/Service'

export interface IDatabaseUsageDAO {
  docToDatabaseUsage: (doc: string, service: Service) => Promise<DatabaseUsage>
  databaseUsageToDoc: (usage: DatabaseUsage) => any
}
