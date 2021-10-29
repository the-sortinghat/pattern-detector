import { Service } from './Service'
import { System } from './System'

describe(System, () => {
  let system: System

  beforeEach(() => {
    system = System.create('foo')
  })

  describe('create', () => {
    it('returns a System instance', () => {
      expect(system).toBeInstanceOf(System)
    })

    it('returns a System called foo', () => {
      expect(system.name).toEqual('foo')
    })

    it('returns a System with ID', () => {
      expect(system.id).toBeDefined()
    })
  })

  describe('addService', () => {
    let service: Service

    beforeEach(() => {
      service = Service.create('foo')
      system.addService(service)
    })

    it('add the service to the system', () => {
      expect(system.services).toContain(service)
    })
  })
})
