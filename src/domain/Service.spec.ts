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
})
