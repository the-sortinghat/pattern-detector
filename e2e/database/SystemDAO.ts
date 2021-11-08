import { Db, MongoClient } from 'mongodb'

import { SystemDAO } from '../../src/application/database/DAOs/SystemDAO'
import { ServiceDAO } from '../../src/application/database/DAOs/ServiceDAO'
import { OperationDAO } from '../../src/application/database/DAOs/OperationDAO'
import { DatabaseUsageDAO } from '../../src/application/database/DAOs/DatabaseUsageDAO'
import { DatabaseDAO } from '../../src/application/database/DAOs/DatabaseDAO'

import { Database, DatabaseUsage, HTTPVerb, Operation, Service, System } from '../../src/domain'

describe(SystemDAO, () => {
  let mongoClient: MongoClient
  let db: Db
  let dbDao: DatabaseDAO
  let opDao: OperationDAO
  let usageDao: DatabaseUsageDAO
  let svcDao: ServiceDAO
  let sysDao: SystemDAO

  beforeAll(async () => {
    mongoClient = new MongoClient('mongodb://db:27017')
    await mongoClient.connect()
    db = mongoClient.db('SystemDaoTest')
  })

  afterAll(async () => {
    await mongoClient.close()
  })

  beforeEach(() => {
    dbDao = new DatabaseDAO(db)
    usageDao = new DatabaseUsageDAO(dbDao)
    opDao = new OperationDAO()
    svcDao = new ServiceDAO(opDao, usageDao)
    sysDao = new SystemDAO(db, svcDao)

    db.collection('systems').deleteMany({})
  })

  describe('SystemDao.store', () => {
    const sysID = 'fake sys uuid'

    it('queries correctly', async () => {
      const system = generateWholeSystem(sysID)
      await sysDao.store(system)
      const queriedSystem = await db.collection('systems').findOne({ uuid: sysID })
      expect(queriedSystem).toBeDefined()
      // @ts-expect-error
      expect(queriedSystem.uuid).toEqual(sysID)
      // @ts-expect-error
      expect(queriedSystem.services).toBeDefined()
      // @ts-expect-error
      expect(queriedSystem.services.length).toEqual(3)
    })
  })
})

function generateSystem(sysID: string): System {
  return System.create('test system', sysID)
}

function generateServices(): Service[] {
  return [
    Service.create('test service 1', 'abcde'),
    Service.create('test service 2', 'bcdef'),
    Service.create('test service 3', 'cdefg'),
  ]
}

function generateOperations(): Operation[] {
  return [
    Operation.create(HTTPVerb.GET, '/foo'),
    Operation.create(HTTPVerb.POST, '/foo/bar'),
    Operation.create(HTTPVerb.PUT, '/foo/id'),
    Operation.create(HTTPVerb.GET, '/foo/baz/bar'),
  ]
}

function generateDatabases(): Database[] {
  return [
    Database.create('fake db 1', 'abcde'),
    Database.create('fake db 2', 'bcdef'),
    Database.create('fake db 3', 'cdefg'),
  ]
}

function generateWholeSystem(sysID: string): System {
  const sys = generateSystem(sysID)

  const svcs = generateServices()

  svcs.forEach((service: Service) => sys.addService(service))

  const ops = generateOperations()

  svcs[0].addOperation(ops[0])
  svcs[1].addOperation(ops[1])
  svcs[1].addOperation(ops[2])
  svcs[2].addOperation(ops[3])

  const dbs = generateDatabases()

  DatabaseUsage.create(svcs[0], dbs[0])
  DatabaseUsage.create(svcs[0], dbs[1])
  DatabaseUsage.create(svcs[1], dbs[1])
  DatabaseUsage.create(svcs[2], dbs[2])

  return sys
}
