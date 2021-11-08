import { IDatabaseUsageDAO } from '../../utils/DatabaseUsageDAO.interface'
import { Service } from '../../../domain/model/Service'
import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { ServiceDAO } from './ServiceDAO'
import { IDatabaseDAO } from '../../utils/DatabaseDAO.interface'
import { Database } from '../../../domain/model/Database'
import {
  generateMockDatabaseDAO,
  generateMockDatabaseUsageDAO,
  generateMockOperationDAO,
  generateService,
  generateServiceDocument,
} from './TestHelpers'

describe(ServiceDAO, () => {
  let svcDao: ServiceDAO
  let opDao: IOperationDAO
  let usageDao: IDatabaseUsageDAO
  let dbDao: IDatabaseDAO

  beforeEach(() => {
    opDao = generateMockOperationDAO()
    usageDao = generateMockDatabaseUsageDAO()
    dbDao = generateMockDatabaseDAO()
    svcDao = new ServiceDAO(opDao, usageDao, dbDao)
  })

  describe('docToService', () => {
    let service: Service
    let doc: any

    describe('with db usages', () => {
      beforeEach(async () => {
        doc = generateServiceDocument({ databaseUsages: true })
        // @ts-expect-error
        dbDao.findOne.mockImplementationOnce((id: string) =>
          Promise.resolve(Database.create('mock db', id)),
        )
        service = await svcDao.docToService(doc)
      })

      it('calls databaseDAO findOne', () => {
        expect(dbDao.findOne).toHaveBeenCalled()
      })

      it('returns a link with a database', () => {
        expect(service.usages.length).toBeGreaterThan(0)
        expect(service.usages[0].ofDatabase).toBeInstanceOf(Database)
      })
    })

    describe('with underneath operations', () => {
      beforeEach(async () => {
        doc = generateServiceDocument({ operations: true })
        service = await svcDao.docToService(doc)
      })

      it('calls operationDAO docToOperation', () => {
        expect(opDao.docToOperation).toHaveBeenCalled()
      })
    })

    describe('without underneath operations nor db usages', () => {
      beforeEach(async () => {
        doc = generateServiceDocument({ operations: false, databaseUsages: false })
        service = await svcDao.docToService(doc)
      })

      it('returns the right entity structure', () => {
        expect(service.name).toEqual(doc.name)
      })

      it('converts uuid into id', () => {
        expect(service.id).toEqual(doc.uuid)
      })

      it('does not call operationDAO docToOperation', () => {
        expect(opDao.docToOperation).not.toHaveBeenCalled()
      })
    })
  })

  describe('serviceToDoc', () => {
    let service: Service
    let doc: any

    describe('with underneath operations and db usages', () => {
      beforeEach(() => {
        service = generateService({ operations: true, databaseUsages: true })
        doc = svcDao.serviceToDoc(service)
      })

      it('returns the right entity structure', () => {
        expect(doc.name).toEqual(service.name)
      })

      it('converts id to uuid', () => {
        expect(doc.id).toBeUndefined()
      })

      it('calls operationDAO operationToDoc', () => {
        expect(opDao.operationToDoc).toHaveBeenCalled()
      })

      it('calls databaseUsageDAO databaseUsageToDoc', () => {
        expect(usageDao.databaseUsageToDoc).toHaveBeenCalled()
      })
    })

    describe('without underneath operations nor usages', () => {
      beforeEach(() => {
        service = generateService({ operations: false, databaseUsages: false })
        doc = svcDao.serviceToDoc(service)
      })

      it('does not call operationDAO operationToDoc', () => {
        expect(opDao.operationToDoc).not.toHaveBeenCalled()
      })

      it('does not call databaseUsageDAO databaseUsageToDoc', () => {
        expect(usageDao.databaseUsageToDoc).not.toHaveBeenCalled()
      })
    })
  })
})
