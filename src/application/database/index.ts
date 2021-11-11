import { Db } from 'mongodb'

import { setupDB } from './Mongo'
import { DatabaseDAO } from './DAOs/DatabaseDAO'
import { DatabaseUsageDAO } from './DAOs/DatabaseUsageDAO'
import { OperationDAO } from './DAOs/OperationDAO'
import { ServiceDAO } from './DAOs/ServiceDAO'
import { SystemDAO } from './DAOs/SystemDAO'
import { SystemRepository } from './repositories/SystemRepository'
import { IDatabaseUsageDAO } from '../utils/DatabaseUsageDAO.interface'
import { IOperationDAO } from '../utils/OperationDAO.interface'
import { IServiceDAO } from '../utils/ServiceDAO.interface'
import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'
import { ISystemRepository } from '../../domain'
import { Logger } from '../logger/Logger'

export interface IPersistenceSetup {
  db: Db
  databaseDAO: IDatabaseDAO
  datababaseUsageDAO: IDatabaseUsageDAO
  operationDAO: IOperationDAO
  serviceDAO: IServiceDAO
  systemDAO: ISystemDAO
  systemRepository: ISystemRepository
}

export async function setupPersistence(dbName: string, logger: Logger): Promise<IPersistenceSetup> {
  const db = await setupDB(dbName, logger)

  const dbDao = new DatabaseDAO(db)
  const usageDao = new DatabaseUsageDAO(dbDao)
  const opDao = new OperationDAO()
  const svcDao = new ServiceDAO(opDao, usageDao)
  const sysDao = new SystemDAO(db, svcDao)

  const repo = new SystemRepository(sysDao)

  return {
    db,
    databaseDAO: dbDao,
    datababaseUsageDAO: usageDao,
    operationDAO: opDao,
    serviceDAO: svcDao,
    systemDAO: sysDao,
    systemRepository: repo,
  }
}
