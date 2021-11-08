import { IDatabaseDAO } from 'application/utils/DatabaseDAO.interface'
import { Database } from '../../../domain/model/Database'
import { DatabaseUsage } from '../../../domain/model/DatabaseUsage'
import { DatabaseUsageDAO } from './DatabaseUsageDAO'
import {
  generateDatabaseUsageDoc,
  generateDatabaseUsage,
  generateMockDatabaseDAO,
  generateService,
} from './TestHelpers'

describe(DatabaseUsageDAO, () => {
  let usageDao: DatabaseUsageDAO
  let dbDao: IDatabaseDAO

  beforeEach(() => {
    dbDao = generateMockDatabaseDAO()
    usageDao = new DatabaseUsageDAO(dbDao)
  })

  describe('docToDatabaseUsage', () => {
    let usage: DatabaseUsage
    let doc: any

    beforeEach(async () => {
      // @ts-expect-error
      dbDao.findOne.mockImplementationOnce((id: string) =>
        Promise.resolve(Database.create('mock db', id)),
      )
      const svc = generateService({ operations: false, databaseUsages: false })
      doc = generateDatabaseUsageDoc()
      usage = await usageDao.docToDatabaseUsage(doc, svc)
    })

    it('returns the pair', () => {
      expect(usage.ofDatabase.id).toEqual(doc)
    })
  })

  describe('databaseUsageToDoc', () => {
    let usage: DatabaseUsage
    let doc: any

    beforeEach(() => {
      usage = generateDatabaseUsage()
      doc = usageDao.databaseUsageToDoc(usage)
    })

    it('returns the right doc', () => {
      expect(doc).toEqual(usage.ofDatabase.id)
    })
  })
})
