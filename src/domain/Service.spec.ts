import { Database } from './Database'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from './MeasuresVessel'
import { HTTPVerb, Operation } from './Operation'
import { Service } from './Service'

describe(Service, () => {
  let service: Service

  beforeEach(() => {
    service = Service.create('foo')
  })

  describe('create', () => {
    it('returns a Service instance', () => {
      expect(service).toBeInstanceOf(Service)
    })

    it('returns a Service called foo', () => {
      expect(service.name).toEqual('foo')
    })

    it('returns a Service with ID', () => {
      expect(service.id).toBeDefined()
    })
  })

  describe('measuresVessel', () => {
    it('exists', () => {
      expect(service.measuresVessel).toBeInstanceOf(MeasuresVessel)
    })
  })

  describe('addOperation', () => {
    let operation: Operation

    beforeEach(() => {
      operation = Operation.create(HTTPVerb.GET, '/foo')
      service.addOperation(operation)
    })

    it('adds the operation to the service', () => {
      expect(service.operations).toContain(operation)
    })
  })

  describe('addUsage', () => {
    let usage: DatabaseUsage

    beforeEach(() => {
      const of = Database.create('of-db')

      usage = DatabaseUsage.create(service, of)
    })

    it('adds a reference to the usage', () => {
      expect(service.usages).toContain(usage)
    })
  })
})
