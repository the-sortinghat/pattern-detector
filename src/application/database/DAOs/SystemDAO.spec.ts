import { SystemDAO } from './SystemDAO'
import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { System } from '../../../domain/model/System'

interface SystemMockConfig {
  services: boolean
}

interface MockedCollection {
  findOne: jest.Mock
}

function generateSystemDocument({ services }: SystemMockConfig) {
  let svcs: any[] = []

  if (services) svcs = [{ name: 'Mock Service', uuid: 'fake uuid' }]

  return {
    name: 'Mock Document',
    uuid: 'fake uuid',
    services: svcs,
  }
}

function generateMockServiceDAO(): IServiceDAO {
  return {
    store: jest.fn(),
    findOne: jest.fn(),
    docToService: jest.fn(),
  }
}

function generateMockCollection(): MockedCollection {
  return {
    findOne: jest.fn(),
  }
}

describe(SystemDAO, () => {
  let sysDao: SystemDAO
  let svcDao: IServiceDAO
  let mockCollection: MockedCollection

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

      it('calls serviceDAO docToService', () => {
        expect(svcDao.docToService).not.toHaveBeenCalled()
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

  describe.skip('systemToDoc', () => {})
  describe.skip('store', () => {})
})
