import { KafkaController } from './application/reactive/KafkaController'
import { Kafka } from './application/reactive/Kafka'
import { ISystemRepository } from './domain/utils/SystemRepository.interface'
import { System } from './domain/model/System'
import { setupDB } from './application/database/Mongo'

const kafka = Kafka.inst

const consumer = kafka.createConsumer('hackathon')
const producer = kafka.createProducer()

class InMemorySystemRepository implements ISystemRepository {
  private systems: System[] = []

  public save(system: System): Promise<System> {
    this.systems.push(system)
    console.log(this.systems)
    return new Promise((res) => res(system))
  }

  public update(sID: string, updated: System): Promise<System> {
    const sysIndex = this.systems.findIndex(({ id }: System) => id === sID)
    return new Promise((res, rej) => {
      if (sysIndex < 0) rej('not found')
      else {
        this.systems[sysIndex] = updated
        res(updated)
      }
    })
  }

  public findOne(sID: string): Promise<System> {
    const sys = this.systems.find(({ id }: System) => id === sID)
    return new Promise((res, rej) => {
      if (!sys) rej('not found')
      else res(sys)
    })
  }
}

setupDB()

const kafkaCtrl = new KafkaController(new InMemorySystemRepository())

consumer.on('message', kafkaCtrl.createSystem.bind(kafkaCtrl))

consumer.on('error', kafkaCtrl.printError.bind(kafkaCtrl))

function newMessage(topic: string, payload: Record<string, any>) {
  return {
    topic,
    messages: JSON.stringify(payload),
  }
}

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
