import { Database } from '../../../domain'

import { DatabaseDAO } from './DatabaseDAO'
import {
  IMockedCollection,
  generateMockCollection,
  generateDatabaseDocument,
  generateDatabase,
} from './TestHelpers'

describe(DatabaseDAO, () => {
  let dbDao: DatabaseDAO
  let mockCollection: IMockedCollection

  beforeEach(() => {
    mockCollection = generateMockCollection()
    const mockDb = { collection: () => mockCollection }
    // @ts-expect-error
    dbDao = new DatabaseDAO(mockDb)
  })

  describe('docToDatabase', () => {
    let db: Database
    let doc: any

    beforeEach(() => {
      doc = generateDatabaseDocument()
      db = dbDao.docToDatabase(doc)
    })

    it('returns the right entity structure', () => {
      expect(db.make).toEqual(doc.make)
    })

    it('converts uuid into id', () => {
      expect(db.id).toEqual(doc.uuid)
    })
  })

  describe('databaseToDoc', () => {
    let db: Database
    let doc: any

    beforeEach(() => {
      db = generateDatabase()
      doc = dbDao.databaseToDoc(db)
    })

    it('returns the right entity structure', () => {
      expect(doc.make).toEqual(db.make)
    })

    it('converts id to uuid', () => {
      expect(doc.uuid).toEqual(db.id)
      expect(doc.id).toBeUndefined()
    })
  })

  describe('findOne', () => {
    describe('when its empty', () => {
      beforeEach(() => {
        mockCollection.findOne.mockReturnValueOnce(null)
      })

      it('throws an error when not found', () => {
        expect(() => dbDao.findOne('whatever')).rejects.toMatch('not found')
      })
    })

    describe('when it finds', () => {
      let db: Database
      let mockDoc: any

      beforeEach(async () => {
        const originalParser = dbDao.docToDatabase
        dbDao.docToDatabase = jest.fn(originalParser.bind(dbDao))

        mockDoc = { make: 'Mock DB', uuid: 'mock uuid' }
        mockCollection.findOne.mockReturnValueOnce(mockDoc)
        db = await dbDao.findOne('foo')
      })

      it('returns an instance of Database', () => {
        expect(db).toBeInstanceOf(Database)
      })

      it('returns the properly reconstruction of Database', () => {
        expect(db.make).toEqual(mockDoc.make)
      })

      it('calls the docToDatabase parser', () => {
        expect(dbDao.docToDatabase).toHaveBeenCalled()
      })
    })
  })

  describe('store', () => {
    let db: Database
    let mockDoc: any

    beforeEach(async () => {
      db = generateDatabase()
      mockDoc = { uuid: db.id }
      dbDao.databaseToDoc = jest.fn((_database: Database) => mockDoc)
      await dbDao.store(db)
    })

    it('parses the database to document', () => {
      expect(dbDao.databaseToDoc).toHaveBeenCalled()
    })

    it('upserts into the collection', () => {
      expect(mockCollection.updateOne).toHaveBeenCalledWith(
        { uuid: db.id },
        { $set: mockDoc },
        {
          upsert: true,
        },
      )
    })
  })
})
