import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { System } from '../../../domain/model/System'
import { Service } from '../../../domain/model/Service'
import { IOperationDAO } from '../../utils/OperationDAO.interface'
import { HTTPVerb, Operation } from '../../../domain/model/Operation'

export interface ISystemMockConfig {
  services: boolean
}

export interface IServiceMockConfig {
  operations: boolean
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

export function generateServiceDocument({ operations }: IServiceMockConfig): any {
  let ops: any[] = []

  if (operations) ops = [{ verb: 'GET', path: '/foo' }]

  return {
    name: 'Mock Service',
    uuid: 'fake uuid',
    operations: ops,
  }
}

export function generateGetOperationDocument(): any {
  return {
    verb: 'GET',
    path: '/foo',
  }
}

export function generateSystem({ services }: ISystemMockConfig): System {
  const system = System.create('Mock System', 'fake uuid')

  if (services) system.addService(Service.create('Mock Service', 'fake uuid'))

  return system
}

export function generateService({ operations }: IServiceMockConfig): Service {
  const service = Service.create('Mock Service', 'fake uuid')

  if (operations) service.addOperation(Operation.create(HTTPVerb.GET, '/foo'))

  return service
}

export function generateGetOperation(): Operation {
  return Operation.create(HTTPVerb.GET, '/foo')
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

export function generateMockCollection(): IMockedCollection {
  return {
    findOne: jest.fn(),
    updateOne: jest.fn(),
  }
}
