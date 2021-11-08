import { Document } from 'mongodb'

import { DatabaseUsage } from '../../domain/model/DatabaseUsage'

export interface IDatabaseUsageDAO {
  docToDatabaseUsage: (doc: Document) => DatabaseUsage
  databaseUsageToDoc: (usage: DatabaseUsage) => any
}
