import { InvalidStateError } from './errors/InvalidStateError'
import { Database } from './Database'
import { DatabaseUsage } from './DatabaseUsage'
import { MeasuresVessel } from '../metrics/MeasuresVessel'
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

  describe('create with invalid names throwing InvalidStateError', () => {
    test('when name is undefined', () => {
      // @ts-ignore
      expect(() => Database.create(undefined)).toThrowError(InvalidStateError)
    })

    test('when name is null', () => {
      // @ts-ignore
      expect(() => Database.create(null)).toThrowError(InvalidStateError)
    })

    test('when name is not a string', () => {
      // @ts-ignore
      expect(() => Database.create(4)).toThrowError(InvalidStateError)
    })

    test('when name is empty string', () => {
      expect(() => Database.create('')).toThrowError(InvalidStateError)
    })
  })

  describe('measuresVessel', () => {
    it('exists', () => {
      expect(database.measuresVessel).toBeInstanceOf(MeasuresVessel)
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
