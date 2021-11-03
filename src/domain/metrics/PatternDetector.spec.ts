import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { HTTPVerb, Operation } from '../model/Operation'
import { Service } from '../model/Service'
import { System } from '../model/System'
import { PatternDetector } from './PatternDetector'

describe(PatternDetector, () => {
  let detector: PatternDetector

  beforeEach(() => {
    detector = PatternDetector.create()
  })

  describe('visitSystem', () => {
    let system: System
    let service: Service

    beforeEach(() => {
      system = System.create('mock')
      service = Service.create('mock svc')
      service.accept = jest.fn()
      system.addService(service)

      system.accept(detector)
    })

    it('visits the services of the system', () => {
      expect(service.accept).toHaveBeenCalledWith(detector)
    })
  })

  describe('visitService', () => {
    let service: Service
    let op: Operation
    let usage: DatabaseUsage

    beforeEach(() => {
      service = Service.create('target')
      op = Operation.create(HTTPVerb.GET, '/foo')
      op.accept = jest.fn()
      service.addOperation(op)
      const database = Database.create('MockDB')
      database.addUsage = jest.fn()
      usage = DatabaseUsage.create(service, database)
      usage.accept = jest.fn()

      service.accept(detector)
    })

    it('does not visit the operations of the service', () => {
      expect(op.accept).not.toHaveBeenCalledWith(detector)
    })

    it('visits the usages of the service', () => {
      expect(usage.accept).toHaveBeenCalledWith(detector)
    })
  })
})
