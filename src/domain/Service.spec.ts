import { Service } from './Service'

describe(Service, () => {
  describe('create', () => {
    let service: Service

    beforeEach(() => {
      service = Service.create('foo')
    })

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
})
