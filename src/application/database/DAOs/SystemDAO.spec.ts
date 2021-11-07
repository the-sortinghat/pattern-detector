import { SystemDAO } from './SystemDAO'
import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { System } from '../../../domain/model/System'
import { Service } from '../../../domain/model/Service'

import {
  IMockedCollection,
  generateMockCollection,
  generateMockServiceDAO,
  generateSystem,
  generateSystemDocument,
  generateMockOperationDAO,
} from './TestHelpers'
import { ServiceDAO } from './ServiceDAO'

describe(SystemDAO, () => {
  let sysDao: SystemDAO
  let svcDao: IServiceDAO
  let mockCollection: IMockedCollection

  beforeEach(() => {
    mockCollection = generateMockCollection()
    const mockDb = { collection: () => mockCollection }
    svcDao = generateMockServiceDAO()
    // @ts-expect-error
    sysDao = new SystemDAO(mockDb, svcDao)
  })

  describe('docToSystem', () => {
    let system: System
    let doc: any

    describe('with underneath services', () => {
      beforeEach(() => {
        doc = generateSystemDocument({ services: true })
        system = sysDao.docToSystem(doc)
      })

      it('returns the right entity structure', () => {
        expect(system.name).toEqual(doc.name)
      })

      it('converts uuid into id', () => {
        expect(system.id).toEqual(doc.uuid)
      })

      it('calls serviceDAO docToService', () => {
        expect(svcDao.docToService).toHaveBeenCalled()
      })
    })

    describe('without underneath services', () => {
      beforeEach(() => {
        doc = generateSystemDocument({ services: false })
        system = sysDao.docToSystem(doc)
      })

      it('returns the right entity structure', () => {
        expect(system.name).toEqual(doc.name)
      })

      it('does not call serviceDAO docToService', () => {
        expect(svcDao.docToService).not.toHaveBeenCalled()
      })
    })
  })

  describe('systemToDoc', () => {
    let system: System
    let doc: any

    describe('with underneath services', () => {
      beforeEach(() => {
        system = generateSystem({ services: true })
        doc = sysDao.systemToDoc(system)
      })

      it('returns the right entity structure', () => {
        expect(doc.name).toEqual(system.name)
      })

      it('converts id to uuid', () => {
        expect(doc.uuid).toEqual(system.id)
        expect(doc.id).toBeUndefined()
      })

      it('calls serviceDAO serviceToDoc', () => {
        expect(svcDao.serviceToDoc).toHaveBeenCalled()
      })
    })

    describe('without underneath services', () => {
      beforeEach(() => {
        system = generateSystem({ services: false })
        doc = sysDao.systemToDoc(system)
      })

      it('returns the right entity structure', () => {
        expect(doc.name).toEqual(system.name)
      })

      it('does not call serviceDAO serviceToDoc', () => {
        expect(svcDao.serviceToDoc).not.toHaveBeenCalled()
      })
    })
  })

  describe('findOne', () => {
    describe('when its empty', () => {
      beforeEach(() => {
        mockCollection.findOne.mockReturnValueOnce(null)
      })

      it('throws an error when not found', () => {
        expect(() => sysDao.findOne('whatever')).rejects.toMatch('not found')
      })
    })

    describe('when it finds', () => {
      let sys: System
      let mockDoc: any

      beforeEach(async () => {
        const originalParser = sysDao.docToSystem
        sysDao.docToSystem = jest.fn(originalParser.bind(sysDao))

        mockDoc = { name: 'Mock system', uuid: 'mock uuid', services: [] }
        mockCollection.findOne.mockReturnValueOnce(mockDoc)
        sys = await sysDao.findOne('foo')
      })

      it('returns an instance of System', () => {
        expect(sys).toBeInstanceOf(System)
      })

      it('returns the properly reconstruction of System', () => {
        expect(sys.name).toEqual(mockDoc.name)
      })

      it('calls the docToSystem parser', () => {
        expect(sysDao.docToSystem).toHaveBeenCalled()
      })
    })
  })

  describe('findOneService', () => {
    describe('when its empty', () => {
      beforeEach(() => {
        mockCollection.findOne.mockReturnValueOnce(null)
      })

      it('throws an error when not found', () => {
        expect(() => sysDao.findOneService('whatever')).rejects.toMatch('not found')
      })
    })

    describe('when it find', () => {
      let sys: System
      let svc: Service
      let mockDoc: any
      const svcID = 'fake svc uuid'

      beforeEach(async () => {
        const trueSvcDao = new ServiceDAO(generateMockOperationDAO())
        // @ts-expect-error
        svcDao.docToService.mockImplementationOnce(trueSvcDao.docToService.bind(trueSvcDao))
        const originalParser = sysDao.docToSystem
        sysDao.docToSystem = jest.fn(originalParser.bind(sysDao))

        mockDoc = {
          name: 'Mock system',
          uuid: 'mock uuid',
          services: [
            {
              name: 'ACHOU O SERVICE',
              uuid: svcID,
              operations: [],
            },
          ],
        }
        mockCollection.findOne.mockReturnValueOnce(mockDoc)
        const resp = await sysDao.findOneService(svcID)
        sys = resp.parentSystem
        svc = resp.service
      })

      it('returns an instance of System and of Service', () => {
        expect(sys).toBeInstanceOf(System)
        expect(svc).toBeInstanceOf(Service)
      })

      it('returns the properly reconstruction of System', () => {
        expect(svc.name).toEqual(mockDoc.services[0].name)
      })

      it('calls the docToSystem parser', () => {
        expect(sysDao.docToSystem).toHaveBeenCalled()
      })
    })
  })

  describe('store', () => {
    let system: System
    let mockDoc: any

    beforeEach(async () => {
      system = generateSystem({ services: true })
      mockDoc = { uuid: system.id }
      sysDao.systemToDoc = jest.fn((_system: System) => mockDoc)
      await sysDao.store(system)
    })

    it('parses the system to document', () => {
      expect(sysDao.systemToDoc).toHaveBeenCalled()
    })

    it('upserts into the collection', () => {
      expect(mockCollection.updateOne).toHaveBeenCalledWith({ uuid: system.id }, mockDoc, {
        upsert: true,
      })
    })
  })
})
