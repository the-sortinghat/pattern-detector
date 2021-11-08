import { IDatabaseUsageDAO } from '../../utils/DatabaseUsageDAO.interface'
import { Service } from '../../../domain/model/Service'
import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { ServiceDAO } from './ServiceDAO'
import {
  generateMockDatabaseUsageDAO,
  generateMockOperationDAO,
  generateService,
  generateServiceDocument,
} from './TestHelpers'

describe(ServiceDAO, () => {
  let svcDao: ServiceDAO
  let opDao: IOperationDAO
  let usageDao: IDatabaseUsageDAO

  beforeEach(() => {
    opDao = generateMockOperationDAO()
    usageDao = generateMockDatabaseUsageDAO()
    svcDao = new ServiceDAO(opDao, usageDao)
  })

  describe('docToService', () => {
    let service: Service
    let doc: any

    describe('with db usages', () => {
      beforeEach(async () => {
        doc = generateServiceDocument({ databaseUsages: true })
        service = await svcDao.docToService(doc)
      })

      it('calls databaseUsageDAO docToDatabaseUsage', () => {
        expect(usageDao.docToDatabaseUsage).toHaveBeenCalled()
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
