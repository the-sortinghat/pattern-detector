import { System } from '../model/System'
import { Database } from '../model/Database'
import { Service } from '../model/Service'
import { DatabasePerServiceDetector } from './DatabasePerServiceDetector'
import { Metrics } from './MeasuresVessel'
import { HTTPVerb, Operation } from '../model/Operation'
import { DatabaseUsage } from '../model/DatabaseUsage'

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

    beforeEach(() => {
      detector.addCandidate = jest.fn()
    })

    describe('database with 0 usages', () => {
      const nClients = 0

      beforeEach(() => {
        database = generateDatabaseWithMetrics({ nClients })
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
        detector.visitDatabase(database)
      })

      it('does not make into candidates list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(database)
      })
    })
  })

  describe('composeResults', () => {
    beforeEach(() => {
      detector = DatabasePerServiceDetector.create()
    })

    describe('single service', () => {
      beforeEach(() => {
        detector.addCandidate(Service.create('mockado'))
        detector.composeResults()
      })

      it('does not detect DB per Service', () => {
        expect(detector.results).toEqual([])
      })
    })

    describe('single database', () => {
      beforeEach(() => {
        detector.addCandidate(Database.create('mockdb'))
        detector.composeResults()
      })

      it('does not detect DB per Service', () => {
        expect(detector.results).toEqual([])
      })
    })

    describe('single pair of service and database', () => {
      let service: Service
      let database: Database

      beforeEach(() => {
        service = Service.create('mockaccino')
        database = Database.create('mockdb')

        DatabaseUsage.create(service, database)

        detector.addCandidate(service)
        detector.addCandidate(database)

        detector.composeResults()
      })

      it('detects DB per Service', () => {
        expect(detector.results).toEqual([{ serviceID: service.id, databaseID: database.id }])
      })
    })
  })

  describe('the complete detection process', () => {
    let detector: DatabasePerServiceDetector
    let svc3: Service
    let db2: Database

    beforeEach(() => {
      detector = DatabasePerServiceDetector.create()

      const system = System.create('toy')

      const svc1 = Service.create('svc1')
      const svc2 = Service.create('svc2')
      svc3 = Service.create('svc3')

      const op1 = Operation.create(HTTPVerb.GET, '/foo/1')
      const op2 = Operation.create(HTTPVerb.GET, '/foo/2')
      const op3 = Operation.create(HTTPVerb.GET, '/foo/3')
      const op4 = Operation.create(HTTPVerb.GET, '/foo/4')
      const op5 = Operation.create(HTTPVerb.GET, '/foo/5')
      const op6 = Operation.create(HTTPVerb.GET, '/foo/6')

      const db1 = Database.create('mockdb1')
      db2 = Database.create('mockdb2')

      system.addService(svc1)
      system.addService(svc2)
      system.addService(svc3)

      svc1.addOperation(op1)
      svc1.addOperation(op2)
      svc2.addOperation(op3)
      svc3.addOperation(op4)
      svc3.addOperation(op5)
      svc3.addOperation(op6)

      DatabaseUsage.create(svc1, db1)
      DatabaseUsage.create(svc2, db1)
      DatabaseUsage.create(svc3, db2)

      svc1.measuresVessel.increment(Metrics.nOperations, 2)
      svc1.measuresVessel.increment(Metrics.nDatabaseUsing, 1)

      svc2.measuresVessel.increment(Metrics.nOperations, 1)
      svc2.measuresVessel.increment(Metrics.nDatabaseUsing, 1)

      svc3.measuresVessel.increment(Metrics.nOperations, 3)
      svc3.measuresVessel.increment(Metrics.nDatabaseUsing, 1)

      db1.measuresVessel.increment(Metrics.nUsageClients, 2)
      db2.measuresVessel.increment(Metrics.nUsageClients, 1)

      detector.visitSystem(system)
    })

    it('detects DB per Service between svc3 and db2', () => {
      expect(detector.results).toEqual([{ serviceID: svc3.id, databaseID: db2.id }])
    })
  })
})
