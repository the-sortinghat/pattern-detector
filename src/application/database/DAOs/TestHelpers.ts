import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { System } from '../../../domain/model/System'
import { Service } from '../../../domain/model/Service'
import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { HTTPVerb, Operation } from '../../../domain/model/Operation'
import { IDatabaseUsageDAO } from '../../utils/DatabaseUsageDAO.interface'
import { IDatabaseDAO } from '../../utils/DatabaseDAO.interface'
import { Database } from '../../../domain/model/Database'
import { DatabaseUsage } from '../../../domain/model/DatabaseUsage'

export interface ISystemMockConfig {
  services: boolean
}

export interface IServiceMockConfig {
  operations?: boolean
  databaseUsages?: boolean
}

export interface IMockedCollection {
  findOne: jest.Mock
  updateOne: jest.Mock
}

export function generateSystemDocument({ services }: ISystemMockConfig): any {
  let svcs: any[] = []

  if (services) svcs = [{ name: 'Mock Service', uuid: 'fake uuid' }]

  return {
    name: 'Mock Document',
    uuid: 'fake uuid',
    services: svcs,
  }
}

export function generateServiceDocument({ operations, databaseUsages }: IServiceMockConfig): any {
  let ops: any[] = []
  let usages: any[] = []

  if (operations) ops = [{ verb: 'GET', path: '/foo' }]

  if (databaseUsages) usages = ['fake db uuid']

  return {
    name: 'Mock Service',
    uuid: 'fake uuid',
    operations: ops,
    databaseUsages: usages,
  }
}

export function generateGetOperationDocument(): any {
  return {
    verb: 'GET',
    path: '/foo',
  }
}

export function generateDatabaseUsageDoc(): string {
  return 'fake db uuid'
}

export function generateDatabaseDocument(): any {
  return {
    make: 'mock db',
    uuid: 'fake db uuid',
  }
}

export function generateSystem({ services }: ISystemMockConfig): System {
  const system = System.create('Mock System', 'fake uuid')

  if (services) system.addService(Service.create('Mock Service', 'fake uuid'))

  return system
}

export function generateService({ operations, databaseUsages }: IServiceMockConfig): Service {
  const service = Service.create('Mock Service', 'fake uuid')

  if (operations) service.addOperation(Operation.create(HTTPVerb.GET, '/foo'))

  if (databaseUsages) {
    const of = Database.create('mock db', 'fake db uuid')
    DatabaseUsage.create(service, of)
  }

  return service
}

export function generateGetOperation(): Operation {
  return Operation.create(HTTPVerb.GET, '/foo')
}

export function generateDatabaseUsage(): DatabaseUsage {
  return DatabaseUsage.create(
    Service.create('mock service', 'fake svc uuid'),
    Database.create('mock db', 'fake db uuid'),
  )
}

export function generateDatabase(): Database {
  return Database.create('mock db', 'fake db uuid')
}

export function generateMockServiceDAO(): IServiceDAO {
  return {
    docToService: jest.fn(),
    serviceToDoc: jest.fn(),
  }
}

export function generateMockOperationDAO(): IOperationDAO {
  return {
    docToOperation: jest.fn(),
    operationToDoc: jest.fn(),
  }
}

export function generateMockDatabaseUsageDAO(): IDatabaseUsageDAO {
  return {
    docToDatabaseUsage: jest.fn(),
    databaseUsageToDoc: jest.fn(),
  }
}

export function generateMockDatabaseDAO(): IDatabaseDAO {
  return {
    store: jest.fn(),
    findOne: jest.fn(),
    docToDatabase: jest.fn(),
    databaseToDoc: jest.fn(),
  }
}

export function generateMockCollection(): IMockedCollection {
  return {
    findOne: jest.fn(),
    updateOne: jest.fn(),
  }
}
