import { Message } from 'kafka-node'

import { ISystemRepository } from '../domain/utils/SystemRepository.interface'
import { InvalidStateError } from '../domain/model/errors/InvalidStateError'
import { System } from '../domain/model/System'

export class KafkaController {
  constructor(private readonly systemRepository: ISystemRepository) {}
  public printMessage(msgString: Message): void {
    const msg = this.parseMessageValue(msgString)
    console.log(msg)
  }

  public printError(err: any): void {
    console.log(err)
  }

  public async createSystem(msgString: Message): Promise<void> {
    const { name } = this.parseMessageValue(msgString)
    try {
      const system = System.create(name)
      await this.systemRepository.save(system)
    } catch (e) {
      if (e instanceof InvalidStateError) console.log(e.message)
    }
  }

  private parseMessageValue(msg: Message): any {
    return JSON.parse(msg.value as string)
  }
}
