import { Database } from '../model/Database'
import { Service } from '../model/Service'
import { DatabasePerServiceDetector } from './DatabasePerServiceDetector'
import { Metrics } from './MeasuresVessel'

interface IServiceWithPayload {
  nOperations: number
  nUsages: number
}

interface IDatabaseWithPayload {
  nClients: number
}

function generateServiceWithMetrics({ nOperations, nUsages }: IServiceWithPayload): Service {
  const service = Service.create('generated')

  service.measuresVessel.increment(Metrics.nOperations, nOperations)
  service.measuresVessel.increment(Metrics.nDatabaseUsing, nUsages)

  return service
}

function generateDatabaseWithMetrics({ nClients }: IDatabaseWithPayload): Database {
  const database = Database.create('generated')

  database.measuresVessel.increment(Metrics.nUsageClients, nClients)

  return database
}

describe(DatabasePerServiceDetector, () => {
  let detector: DatabasePerServiceDetector

  beforeEach(() => {
    detector = DatabasePerServiceDetector.create()
  })

  describe('visitService', () => {
    let service: Service

    describe('service with few operations and one database usage', () => {
      const maxOperations = 10

      beforeEach(() => {
        service = generateServiceWithMetrics({ nOperations: maxOperations, nUsages: 1 })
        detector.addCandidate = jest.fn()
        detector.visitService(service)
      })

      it('enters as candidate', () => {
        expect(detector.addCandidate).toHaveBeenCalledWith(service)
      })
    })

    describe('service with too many operations and one database usage', () => {
      const tooManyOperations = 11

      beforeEach(() => {
        service = generateServiceWithMetrics({ nOperations: tooManyOperations, nUsages: 1 })
        detector.addCandidate = jest.fn()
        detector.visitService(service)
      })

      it('does not make to the candidate list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(service)
      })
    })

    describe('service with few operations and two databases usage', () => {
      const maxOperations = 10
      const tooManyDBs = 2

      beforeEach(() => {
        service = generateServiceWithMetrics({ nOperations: maxOperations, nUsages: tooManyDBs })
        detector.addCandidate = jest.fn()
        detector.visitService(service)
      })

      it('does not make to the candidate list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(service)
      })
    })

    describe('service with too many operations and two databases usage', () => {
      const tooManyOps = 11
      const tooManyDBs = 2

      beforeEach(() => {
        service = generateServiceWithMetrics({ nOperations: tooManyOps, nUsages: tooManyDBs })
        detector.addCandidate = jest.fn()
        detector.visitService(service)
      })

      it('does not make to the candidate list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(service)
      })
    })
  })

  describe('visitDatabase', () => {
    let database: Database

    describe('database with 0 usages', () => {
      const nClients = 0

      beforeEach(() => {
        database = generateDatabaseWithMetrics({ nClients })
        detector.addCandidate = jest.fn()
        detector.visitDatabase(database)
      })

      it('does not make into candidates list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(database)
      })
    })

    describe('database with 1 usage', () => {
      const nClients = 1

      beforeEach(() => {
        database = generateDatabaseWithMetrics({ nClients })
        detector.addCandidate = jest.fn()
        detector.visitDatabase(database)
      })

      it('does not make into candidates list', () => {
        expect(detector.addCandidate).toHaveBeenCalledWith(database)
      })
    })

    describe('database with 2 usages', () => {
      const nClients = 2

      beforeEach(() => {
        database = generateDatabaseWithMetrics({ nClients })
        detector.addCandidate = jest.fn()
        detector.visitDatabase(database)
      })

      it('does not make into candidates list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(database)
      })
    })
  })
})
