import { IServiceDAO } from '../../utils/ServiceDAO.interface'
import { System } from '../../../domain/model/System'
import { Service } from '../../../domain/model/Service'

export interface ISystemMockConfig {
  services: boolean
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

export function generateSystem({ services }: ISystemMockConfig): System {
  const system = System.create('Mock System', 'fake uuid')

  if (services) system.addService(Service.create('Mock Service', 'fake uuid'))

  return system
}

export function generateMockServiceDAO(): IServiceDAO {
  return {
    findOne: jest.fn(),
    docToService: jest.fn(),
    serviceToDoc: jest.fn(),
  }
}

export function generateMockCollection(): IMockedCollection {
  return {
    findOne: jest.fn(),
    updateOne: jest.fn(),
  }
}
