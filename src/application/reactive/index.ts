import { Consumer, Message } from 'kafka-node'

import { ISystemRepository } from '../../domain'

import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'
import { ICreateDatabaseEventSchema } from './eventSchemas/CreateDatabaseEventSchema.interface'
import { ICreateDatabaseUsageEventSchema } from './eventSchemas/CreateDatabaseUsageEventSchema.interface'
import { ICreateOperationEventSchema } from './eventSchemas/CreateOperationEventSchema.interface'
import { ICreateServiceEventSchema } from './eventSchemas/CreateServiceEventSchema.interface'
import { ICreateSystemEventSchema } from './eventSchemas/CreateSystemEventSchema.interface'
import { Kafka } from './Kafka'
import { EventsController } from './EventsController'

interface Consumers {
  [key: string]: Consumer
}

function ParseMessageWrapper<T>(cb: (payload: T) => Promise<void>) {
  return async (msg: Message) => {
    const payload: T = JSON.parse(msg.value as string)
    await cb(payload)
  }
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

  const kafkaCtrl = new EventsController(sysRepo, sysDao, dbDao)

  Object.values(consumers).forEach((consumer: Consumer) => consumer.on('error', console.log))

  consumers.system.on(
    'message',
    ParseMessageWrapper<ICreateSystemEventSchema>(kafkaCtrl.createSystem.bind(kafkaCtrl)),
  )

  consumers.service.on(
    'message',
    ParseMessageWrapper<ICreateServiceEventSchema>(kafkaCtrl.createService.bind(kafkaCtrl)),
  )

  consumers.database.on(
    'message',
    ParseMessageWrapper<ICreateDatabaseEventSchema>(kafkaCtrl.createDatabase.bind(kafkaCtrl)),
  )

  consumers.operation.on(
    'message',
    ParseMessageWrapper<ICreateOperationEventSchema>(kafkaCtrl.createOperation.bind(kafkaCtrl)),
  )

  consumers.usage.on(
    'message',
    ParseMessageWrapper<ICreateDatabaseUsageEventSchema>(
      kafkaCtrl.createDatabaseUsage.bind(kafkaCtrl),
    ),
  )

  console.log('Event consumers set up!')
}
