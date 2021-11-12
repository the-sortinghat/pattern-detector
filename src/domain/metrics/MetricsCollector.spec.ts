import { System } from '../model/System'
import { Service } from '../model/Service'
import { MetricsCollector } from './MetricsCollector'
import { HTTPVerb, Operation } from '../model/Operation'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { Database } from '../model/Database'

describe(MetricsCollector, () => {
  let collector: MetricsCollector

  beforeEach(() => {
    collector = MetricsCollector.create()
  })

  describe('visitSystem', () => {
    let system: System
    let mockedService: Service

    beforeEach(() => {
      system = System.create('ToBeMocked')
      mockedService = Service.create('TotalMock')
      mockedService.accept = jest.fn()
      system.addService(mockedService)
      collector.visitSystem(system)
    })

    it('visits the services of the system', () => {
      expect(mockedService.accept).toHaveBeenCalledWith(collector)
    })
  })

  describe('visitService', () => {
    let service: Service
    let operation: Operation
    let dbUsage: DatabaseUsage

    beforeEach(() => {
      service = Service.create('Target')
      operation = Operation.create(HTTPVerb.GET, '/foo')
      service.addOperation(operation)

      const database = Database.create('MockDB')
      database.addUsage = jest.fn()

      dbUsage = DatabaseUsage.create(service, database)

      operation.accept = jest.fn()
      dbUsage.accept = jest.fn()

      collector.visitService(service)
    })

    it('visits the operations of the service', () => {
      expect(operation.accept).toHaveBeenCalledWith(collector)
    })

    it('visits the databaseUsages from the service', () => {
      expect(dbUsage.accept).toHaveBeenCalledWith(collector)
    })

    it('marks the metrics vessel of the service with nOperations = 1', () => {
      expect(collector.nOperations(service.id)).toEqual(1)
    })

    it('marks the metrics vessel of the service with nDatabaseUsing = 1', () => {
      expect(collector.nDatabaseUsing(service.id)).toEqual(1)
    })

    it("does not change nUsageClients of the service's metrics vessel", () => {
      expect(collector.nUsageClients(service.id)).toEqual(0)
    })
  })

  describe('visitDatabaseUsage', () => {
    let usage: DatabaseUsage
    let database: Database

    beforeEach(() => {
      const from = Service.create('mock')
      database = Database.create('MockDB')
      database.accept = jest.fn()
      usage = DatabaseUsage.create(from, database)
      collector.visitDatabaseUsage(usage)
    })

    it('visits the services of the system', () => {
      expect(database.accept).toHaveBeenCalledWith(collector)
    })
  })

  describe('visitDatabase', () => {
    let database: Database

    beforeEach(() => {
      database = Database.create('MockDB')
      const from = Service.create('from')
      DatabaseUsage.create(from, database)
      collector.visitDatabase(database)
    })

    it('marks the metrics vessel of the database with nUsageClients = 1', () => {
      expect(collector.nUsageClients(database.id)).toEqual(1)
    })

    it("does not change nDatabaseUsing of the database's metrics vessel", () => {
      expect(collector.nDatabaseUsing(database.id)).toEqual(0)
    })

    it("does not change nOperations of the database's metrics vessel", () => {
      expect(collector.nOperations(database.id)).toEqual(0)
    })
  })

  describe('the integrated operation', () => {
    let service: Service
    let database: Database

    beforeEach(() => {
      const system = System.create('Integration System')
      service = Service.create('Service', '1')
      system.addService(service)
      service.addOperation(Operation.create(HTTPVerb.GET, '/foo'))
      database = Database.create('DatabaseDB', '2')
      DatabaseUsage.create(service, database)

      system.accept(collector)
    })

    it('counts nOperations = 1 for the service', () => {
      expect(collector.nOperations(service.id)).toEqual(1)
    })

    it('counts nDatabaseUsing = 1 for the service', () => {
      expect(collector.nDatabaseUsing(service.id)).toEqual(1)
    })

    it('counts nUsageClients = 1 for the database', () => {
      expect(collector.nUsageClients(database.id)).toEqual(1)
    })
  })
})
