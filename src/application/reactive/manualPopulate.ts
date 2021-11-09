import { randomUUID } from 'crypto'
import { Kafka } from './Kafka'

function newMessage(topic: string, payload: Record<string, any>) {
  return {
    topic,
    messages: JSON.stringify(payload),
  }
}

function manualPopulate(): void {
  const kafka = Kafka.inst
  const producer = kafka.createProducer()

  const sysID = 'fake uuid'
  const svcID = randomUUID()
  const dbID = randomUUID()

  const messages = [
    newMessage('new.system', {
      name: 'Fakr',
      id: sysID,
    }),

    newMessage('new.service', {
      name: 'Fakr svc',
      id: svcID,
      systemID: sysID,
    }),

    newMessage('new.database', {
      make: 'FakrDB',
      id: dbID,
    }),

    newMessage('new.operation', {
      verb: 'GET',
      path: '/fakes',
      serviceID: svcID,
    }),

    newMessage('new.usage', {
      serviceID: svcID,
      databaseID: dbID,
    }),
  ]

  producer.on('ready', () => {
    messages.forEach((message: any, i: number) => {
      const time = i * 2500

      setTimeout(() => {
        producer.send([message], (error, data) => {
          if (error) console.log(error)
          else console.log(data)
        })
      }, time)
    })
  })

  producer.on('error', (err) => {
    console.log(err)
  })
}

manualPopulate()
