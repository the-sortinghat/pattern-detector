import { Message } from 'kafka-node'

import { ISystemRepository } from '../../domain/utils/SystemRepository.interface'
import { InvalidStateError } from '../../domain/model/errors/InvalidStateError'
import { System } from '../../domain/model/System'
import { Service } from '../../domain/model/Service'
import { Database } from '../../domain/model/Database'
import { HTTPVerb, Operation } from '../../domain/model/Operation'
import { DatabaseUsage } from '../../domain/model/DatabaseUsage'
import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'

export class KafkaController {
  constructor(
    private readonly systemRepository: ISystemRepository,
    private readonly systemDao: ISystemDAO,
    private readonly databaseDAO: IDatabaseDAO,
  ) {}
  public printMessage(msgString: Message): void {
    const msg = this.parseMessageValue(msgString)
    console.log(msg)
  }

  public printError(err: any): void {
    console.log(err)
  }

  public async createSystem(msgString: Message): Promise<void> {
    const { name, id } = this.parseMessageValue(msgString)
    try {
      const system = System.create(name, id)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e.message)
    }
  }

  public async createService(msgString: Message): Promise<void> {
    const { name, id, systemID } = this.parseMessageValue(msgString)
    try {
      const system = await this.systemRepository.findOne(systemID)
      const service = Service.create(name, id)
      system.addService(service)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e)
    }
  }

  public async createOperation(msgString: Message): Promise<void> {
    const { verb, path, serviceID } = this.parseMessageValue(msgString)
    try {
      const actualVerb = this.parseVerb(verb)
      const operation = Operation.create(actualVerb, path)
      const { parentSystem: system, service } = await this.systemDao.findOneService(serviceID)
      service.addOperation(operation)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e.message)
    }
  }

  public async createDatabase(msgString: Message): Promise<void> {
    const { make, id } = this.parseMessageValue(msgString)
    try {
      const database = Database.create(make, id)
      await this.databaseDAO.store(database)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e.message)
    }
  }

  public async createDatabaseUsage(msgString: Message): Promise<void> {
    const { serviceID, databaseID } = this.parseMessageValue(msgString)
    try {
      const { parentSystem: system, service } = await this.systemDao.findOneService(serviceID)
      const database = await this.databaseDAO.findOne(databaseID)
      DatabaseUsage.create(service, database)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e.message)
    }
  }

  private parseMessageValue(msg: Message): any {
    return JSON.parse(msg.value as string)
  }

  private parseVerb(verb: string): HTTPVerb {
    const verbUpper = verb.toUpperCase()

    switch (verbUpper) {
      case 'GET':
        return HTTPVerb.GET
      case 'POST':
        return HTTPVerb.POST
      case 'PUT':
        return HTTPVerb.PUT
      case 'DELETE':
        return HTTPVerb.DELETE
      case 'PATCH':
        return HTTPVerb.PATCH
      default:
        return HTTPVerb.GET
    }
  }
}
