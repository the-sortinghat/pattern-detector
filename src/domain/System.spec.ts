import { System } from './System'

describe(System, () => {
  describe('create', () => {
    let system: System

    beforeEach(() => {
      system = System.create('foo')
    })

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
})
