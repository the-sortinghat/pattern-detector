import { InvalidStateError } from './errors/InvalidStateError'
import { Database } from './Database'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
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

  describe('create with invalid names throwing InvalidStateError', () => {
    test('when name is undefined', () => {
      // @ts-ignore
      expect(() => Service.create(undefined)).toThrowError(InvalidStateError)
    })

    test('when name is null', () => {
      // @ts-ignore
      expect(() => Service.create(null)).toThrowError(InvalidStateError)
    })

    test('when name is not a string', () => {
      // @ts-ignore
      expect(() => Service.create(4)).toThrowError(InvalidStateError)
    })

    test('when name is empty string', () => {
      expect(() => Service.create('')).toThrowError(InvalidStateError)
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
