import { Consumer } from 'kafka-node'

import { ISystemRepository } from '../../domain'

import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'
import { Kafka } from './Kafka'
import { KafkaController } from './KafkaController'

interface Consumers {
  [key: string]: Consumer
}

export function setupReactiveApp(
  sysRepo: ISystemRepository,
  sysDao: ISystemDAO,
  dbDao: IDatabaseDAO,
): void {
  const consumers: Consumers = {
    system: new Kafka().createConsumer('new.system'),
    service: new Kafka().createConsumer('new.service'),
    database: new Kafka().createConsumer('new.database'),
    operation: new Kafka().createConsumer('new.operation'),
    usage: new Kafka().createConsumer('new.usage'),
  }

  const kafkaCtrl = new KafkaController(sysRepo, sysDao, dbDao)

  Object.keys(consumers).forEach((key: string) =>
    consumers[key].on('error', kafkaCtrl.printError.bind(kafkaCtrl)),
  )

  consumers.system.on('message', kafkaCtrl.createSystem.bind(kafkaCtrl))
  consumers.service.on('message', kafkaCtrl.createService.bind(kafkaCtrl))
  consumers.database.on('message', kafkaCtrl.createDatabase.bind(kafkaCtrl))
  consumers.operation.on('message', kafkaCtrl.createOperation.bind(kafkaCtrl))
  consumers.usage.on('message', kafkaCtrl.createDatabaseUsage.bind(kafkaCtrl))

  console.log('Event consumers set up!')
}
