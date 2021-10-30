import { Database } from './Database'
import { DatabaseUsage } from './DatabaseUsage'
import { Service } from './Service'

describe(DatabaseUsage, () => {
  let usage: DatabaseUsage
  let database: Database
  let service: Service

  beforeEach(() => {
    database = Database.create('foo-db')
    service = Service.create('foo')

    database.addUsage = jest.fn()
    service.addUsage = jest.fn()

    usage = DatabaseUsage.create(service, database)
  })

  describe('create', () => {
    it('returns a DatabaseUsage instance', () => {
      expect(usage).toBeInstanceOf(DatabaseUsage)
    })

    it('returns a DatabaseUsage for foo service', () => {
      expect(usage.fromService.name).toEqual('foo')
    })

    it('returns a DatabaseUsage of a foo-db database', () => {
      expect(usage.ofDatabase.make).toEqual('foo-db')
    })

    it('returns a DatabaseUsage without ID', () => {
      // @ts-ignore
      expect(usage.id).toBeUndefined()
    })

    it('calls service.addUsage', () => {
      expect(service.addUsage).toHaveBeenCalled()
    })

    it('calls database.addUsage', () => {
      expect(database.addUsage).toHaveBeenCalled()
    })
  })
})
