import { Database } from './Database'
import { DatabaseUsage } from './DatabaseUsage'
import { Service } from './Service'

describe(Database, () => {
  let database: Database

  beforeEach(() => {
    database = Database.create('foo')
  })

  describe('create', () => {
    it('returns a Database instance', () => {
      expect(database).toBeInstanceOf(Database)
    })

    it('returns a Database called foo', () => {
      expect(database.make).toEqual('foo')
    })

    it('returns a Database with ID', () => {
      expect(database.id).toBeDefined()
    })
  })

  describe('addUsage', () => {
    let usage: DatabaseUsage

    beforeEach(() => {
      const from = Service.create('from')

      usage = DatabaseUsage.create(from, database)
    })

    it('adds a reference to the usage', () => {
      expect(database.usages).toContain(usage)
    })
  })
})
