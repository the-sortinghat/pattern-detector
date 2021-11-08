import { ISystemRepository } from '../../domain'

import { IDatabaseDAO } from '../utils/DatabaseDAO.interface'
import { ISystemDAO } from '../utils/SystemDAO.interface'
import { Kafka } from './Kafka'
import { KafkaController } from './KafkaController'

function newMessage(topic: string, payload: Record<string, any>) {
  return {
    topic,
    messages: JSON.stringify(payload),
  }
}

export function setupReactiveApp(
  sysRepo: ISystemRepository,
  sysDao: ISystemDAO,
  dbDao: IDatabaseDAO,
): void {
  const kafka = Kafka.inst

  const consumer = kafka.createConsumer('hackathon')
  const producer = kafka.createProducer()

  const kafkaCtrl = new KafkaController(sysRepo, sysDao, dbDao)

  consumer.on('message', kafkaCtrl.createSystem.bind(kafkaCtrl))

  consumer.on('error', kafkaCtrl.printError.bind(kafkaCtrl))

  const messages = [
    newMessage('hackathon', {
      name: 'padathon',
    }),
  ]

  producer.on('ready', () => {
    producer.send(messages, (error, data) => {
      if (error) console.log(error)
      else console.log(data)
    })
  })

  producer.on('error', (err) => {
    console.log(err)
  })
}
