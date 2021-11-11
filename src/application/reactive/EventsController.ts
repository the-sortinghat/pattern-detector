import {
  Database,
  DatabaseUsage,
  InvalidStateError,
  ISystemRepository,
  Operation,
  Service,
  System,
} from '../../domain'

import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'
import { OperationDAO } from '../database/DAOs/OperationDAO'

import { ICreateDatabaseEventSchema } from './eventSchemas/CreateDatabaseEventSchema.interface'
import { ICreateDatabaseUsageEventSchema } from './eventSchemas/CreateDatabaseUsageEventSchema.interface'
import { ICreateOperationEventSchema } from './eventSchemas/CreateOperationEventSchema.interface'
import { ICreateServiceEventSchema } from './eventSchemas/CreateServiceEventSchema.interface'
import { ICreateSystemEventSchema } from './eventSchemas/CreateSystemEventSchema.interface'
import { Logger } from '../logger/Logger'

export class EventsController {
  constructor(
    private readonly systemRepository: ISystemRepository,
    private readonly systemDao: ISystemDAO,
    private readonly databaseDAO: IDatabaseDAO,
    private readonly logger: Logger,
  ) {}

  public async createSystem({ name, id }: ICreateSystemEventSchema): Promise<void> {
    this.logger.debug('[EventsController.createSystem] received:', { name, id })
    try {
      const system = System.create(name, id)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) this.logger.error(e.message)
    }
  }

  public async createService({ name, id, systemID }: ICreateServiceEventSchema): Promise<void> {
    this.logger.debug('[EventsController.createService] received:', { name, id, systemID })
    try {
      const system = await this.systemRepository.findOne(systemID)
      const service = Service.create(name, id)
      system.addService(service)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) this.logger.error(e.message)
    }
  }

  public async createOperation({
    verb,
    path,
    serviceID,
  }: ICreateOperationEventSchema): Promise<void> {
    this.logger.debug('[EventsController.createOperation] received:', { verb, path, serviceID })
    try {
      const actualVerb = OperationDAO.verbStringToHTTPVerb(verb)
      const operation = Operation.create(actualVerb, path)
      const { parentSystem: system, service } = await this.systemDao.findOneService(serviceID)
      service.addOperation(operation)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) this.logger.error(e.message)
    }
  }

  public async createDatabase({ make, id }: ICreateDatabaseEventSchema): Promise<void> {
    this.logger.debug('[EventsController.createDatabase] received:', { make, id })
    try {
      const database = Database.create(make, id)
      await this.databaseDAO.store(database)
    } catch (e) {
      if (e instanceof InvalidStateError) this.logger.error(e.message)
    }
  }

  public async createDatabaseUsage({
    serviceID,
    databaseID,
  }: ICreateDatabaseUsageEventSchema): Promise<void> {
    this.logger.debug('[EventsController.createDatabaseUsage] received:', { serviceID, databaseID })
    try {
      const { parentSystem: system, service } = await this.systemDao.findOneService(serviceID)
      const database = await this.databaseDAO.findOne(databaseID)
      DatabaseUsage.create(service, database)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) this.logger.error(e.message)
    }
  }
}
