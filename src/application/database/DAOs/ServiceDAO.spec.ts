import { Service } from '../../../domain/model/Service'
import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { ServiceDAO } from './ServiceDAO'
import { generateMockOperationDAO, generateService, generateServiceDocument } from './TestHelpers'

describe(ServiceDAO, () => {
  let svcDao: ServiceDAO
  let opDao: IOperationDAO

  beforeEach(() => {
    opDao = generateMockOperationDAO()
    svcDao = new ServiceDAO(opDao)
  })

  describe('docToService', () => {
    let service: Service
    let doc: any

    describe('with underneath operations', () => {
      beforeEach(() => {
        doc = generateServiceDocument({ operations: true })
        service = svcDao.docToService(doc)
      })

      it('returns the right entity structure', () => {
        expect(service.name).toEqual(doc.name)
      })

      it('converts uuid into id', () => {
        expect(service.id).toEqual(doc.uuid)
      })

      it('calls operationDAO docToOperation', () => {
        expect(opDao.docToOperation).toHaveBeenCalled()
      })
    })

    describe('without underneath operations', () => {
      beforeEach(() => {
        doc = generateServiceDocument({ operations: false })
        service = svcDao.docToService(doc)
      })

      it('does not call operationDAO docToOperation', () => {
        expect(opDao.docToOperation).not.toHaveBeenCalled()
      })
    })
  })

  describe('serviceToDoc', () => {
    let service: Service
    let doc: any

    describe('with underneath operations', () => {
      beforeEach(() => {
        service = generateService({ operations: true })
        doc = svcDao.serviceToDoc(service)
      })

      it('returns the right entity structure', () => {
        expect(doc.name).toEqual(service.name)
      })

      it('converts id to uuid', () => {
        expect(doc.uuid).toEqual(service.id)
        expect(doc.id).toBeUndefined()
      })

      it('calls operationDAO operationToDoc', () => {
        expect(opDao.operationToDoc).toHaveBeenCalled()
      })
    })

    describe('without underneath operations', () => {
      beforeEach(() => {
        service = generateService({ operations: false })
        doc = svcDao.serviceToDoc(service)
      })

      it('does not call operationDAO operationToDoc', () => {
        expect(opDao.operationToDoc).not.toHaveBeenCalled()
      })
    })
  })
})
