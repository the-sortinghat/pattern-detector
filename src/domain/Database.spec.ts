import { Database } from './Database'

describe(Database, () => {
  describe('create', () => {
    let database: Database

    beforeEach(() => {
      database = Database.create('foo')
    })

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
})
