import { System } from '../model/System'
import { Database } from '../model/Database'
import { Service } from '../model/Service'
import { DatabasePerServiceDetector } from './DatabasePerServiceDetector'
import { MeasuresVessel, Metrics } from './MeasuresVessel'
import { HTTPVerb, Operation } from '../model/Operation'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { IObjectVessels } from './MetricsCollector'

interface IServiceWithPayload {
  nOperations: number
  nUsages: number
}

interface IDatabaseWithPayload {
  nClients: number
}

interface IServiceWithVessel {
  service: Service
  vessel: MeasuresVessel
}

interface IDatabaseWithVessel {
  database: Database
  vessel: MeasuresVessel
}

function generateServiceWithMetrics({
  nOperations,
  nUsages,
}: IServiceWithPayload): IServiceWithVessel {
  const service = Service.create('generated')
  const vessel = new MeasuresVessel()

  vessel.increment(Metrics.nOperations, nOperations)
  vessel.increment(Metrics.nDatabaseUsing, nUsages)

  return { service, vessel }
}

function generateDatabaseWithMetrics({ nClients }: IDatabaseWithPayload): IDatabaseWithVessel {
  const database = Database.create('generated')
  const vessel = new MeasuresVessel()

  vessel.increment(Metrics.nUsageClients, nClients)

  return { database, vessel }
}

describe(DatabasePerServiceDetector, () => {
  let detector: DatabasePerServiceDetector
  describe('visitService', () => {
    let service: Service

    describe('service with few operations and one database usage', () => {
      const maxOperations = 10

      beforeEach(() => {
        const sWithVessel = generateServiceWithMetrics({ nOperations: maxOperations, nUsages: 1 })
        service = sWithVessel.service

        const vessels: IObjectVessels = {}
        vessels[service.id] = sWithVessel.vessel
        detector = DatabasePerServiceDetector.create(vessels)
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
        const sWithVessel = generateServiceWithMetrics({
          nOperations: tooManyOperations,
          nUsages: 1,
        })
        service = sWithVessel.service
        const vessels: IObjectVessels = {}
        vessels[service.id] = sWithVessel.vessel
        detector = DatabasePerServiceDetector.create(vessels)
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
        const sWithVessel = generateServiceWithMetrics({
          nOperations: maxOperations,
          nUsages: tooManyDBs,
        })
        service = sWithVessel.service
        const vessels: IObjectVessels = {}
        vessels[service.id] = sWithVessel.vessel
        detector = DatabasePerServiceDetector.create(vessels)
        detector.addCandidate = jest.fn()
        detector.visitService(service)
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
        const sWithVessel = generateServiceWithMetrics({
          nOperations: tooManyOps,
          nUsages: tooManyDBs,
        })
        service = sWithVessel.service
        const vessels: IObjectVessels = {}
        vessels[service.id] = sWithVessel.vessel
        detector = DatabasePerServiceDetector.create(vessels)
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
        const dWithVessels = generateDatabaseWithMetrics({ nClients })
        database = dWithVessels.database
        const vessels: IObjectVessels = {}
        vessels[database.id] = dWithVessels.vessel
        detector = DatabasePerServiceDetector.create(vessels)
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
        const dWithVessels = generateDatabaseWithMetrics({ nClients })
        database = dWithVessels.database
        const vessels: IObjectVessels = {}
        vessels[database.id] = dWithVessels.vessel
        detector = DatabasePerServiceDetector.create(vessels)
        detector.addCandidate = jest.fn()
        detector.visitDatabase(database)
      })

      it('enters as candidate', () => {
        expect(detector.addCandidate).toHaveBeenCalledWith(database)
      })
    })

    describe('database with 2 usages', () => {
      const nClients = 2

      beforeEach(() => {
        const dWithVessels = generateDatabaseWithMetrics({ nClients })
        database = dWithVessels.database
        const vessels: IObjectVessels = {}
        vessels[database.id] = dWithVessels.vessel
        detector = DatabasePerServiceDetector.create(vessels)
        detector.addCandidate = jest.fn()
        detector.visitDatabase(database)
      })

      it('does not make into candidates list', () => {
        expect(detector.addCandidate).not.toHaveBeenCalledWith(database)
      })
    })
  })

  describe('composeResults', () => {
    describe('single service', () => {
      beforeEach(() => {
        const svc = Service.create('mockado')

        const vessels: IObjectVessels = {}
        vessels[svc.id] = new MeasuresVessel()
        vessels[svc.id].increment(Metrics.nOperations)

        detector = DatabasePerServiceDetector.create(vessels)
        detector.addCandidate(svc)
        detector.composeResults()
      })

      it('does not detect DB per Service', () => {
        expect(detector.results).toEqual([])
      })
    })

    describe('single database', () => {
      beforeEach(() => {
        const db = Database.create('mockdb')

        const vessels: IObjectVessels = {}
        vessels[db.id] = new MeasuresVessel()

        detector = DatabasePerServiceDetector.create(vessels)
        detector.addCandidate(db)
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

        const vessels: IObjectVessels = {}
        vessels[service.id] = new MeasuresVessel()
        vessels[database.id] = new MeasuresVessel()

        vessels[service.id].increment(Metrics.nOperations)
        vessels[service.id].increment(Metrics.nDatabaseUsing)
        vessels[database.id].increment(Metrics.nUsageClients)

        DatabaseUsage.create(service, database)

        detector = DatabasePerServiceDetector.create(vessels)

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
    let svc3: Service
    let db2: Database

    beforeEach(() => {
      detector = DatabasePerServiceDetector.create({})

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

      const vessels: IObjectVessels = {}

      vessels[svc1.id] = new MeasuresVessel()
      vessels[svc2.id] = new MeasuresVessel()
      vessels[svc3.id] = new MeasuresVessel()
      vessels[db1.id] = new MeasuresVessel()
      vessels[db2.id] = new MeasuresVessel()

      vessels[svc1.id].increment(Metrics.nOperations, 2)
      vessels[svc1.id].increment(Metrics.nDatabaseUsing, 1)

      vessels[svc2.id].increment(Metrics.nOperations, 1)
      vessels[svc2.id].increment(Metrics.nDatabaseUsing, 1)

      vessels[svc3.id].increment(Metrics.nOperations, 3)
      vessels[svc3.id].increment(Metrics.nDatabaseUsing, 1)

      vessels[db1.id].increment(Metrics.nUsageClients, 2)
      vessels[db2.id].increment(Metrics.nUsageClients, 1)

      detector = DatabasePerServiceDetector.create(vessels)

      detector.visitSystem(system)
    })

    it('detects DB per Service between svc3 and db2', () => {
      expect(detector.results).toEqual([{ serviceID: svc3.id, databaseID: db2.id }])
    })
  })
})
