import { Producer } from 'kafka-node'
import { Kafka } from './Kafka'

function newMessage(topic: string, payload: Record<string, any>) {
  return {
    topic,
    messages: JSON.stringify(payload),
  }
}

function sendCallback(error: any, data: any) {
  if (error) console.log(error)
  else console.log(data)
}

function sleep(milis = 250): Promise<void> {
  return new Promise((res) => {
    setTimeout(() => res(), milis)
  })
}

async function newSystem(producer: Producer, name: string, id: string) {
  const messages = [newMessage('new.system', { name, id })]
  await sleep()
  producer.send(messages, sendCallback)
}

async function newService(producer: Producer, name: string, id: string, systemID: string) {
  const messages = [newMessage('new.service', { name, id, systemID })]
  await sleep()
  producer.send(messages, sendCallback)
}

async function newDatabase(producer: Producer, make: string, id: string) {
  const messages = [newMessage('new.database', { make, id })]
  await sleep()
  producer.send(messages, sendCallback)
}

async function newOperation(producer: Producer, verb: string, path: string, serviceID: string) {
  const messages = [newMessage('new.operation', { verb, path, serviceID })]
  await sleep()
  producer.send(messages, sendCallback)
}

async function newDBUsage(producer: Producer, serviceID: string, databaseID: string) {
  const messages = [newMessage('new.usage', { serviceID, databaseID })]
  await sleep()
  producer.send(messages, sendCallback)
}

async function manualPopulate(): Promise<void> {
  const kafka = new Kafka()
  const producer = kafka.createProducer()
  producer.on('error', console.log)

  await newSystem(producer, 'Pingr', '1')

  await newService(producer, 'Account', '1', '1')
  await newService(producer, 'Connections', '2', '1')
  await newService(producer, 'Content', '3', '1')
  await newService(producer, 'Recommendation', '4', '1')
  await newService(producer, 'Ping', '5', '1')
  await newService(producer, 'Interaction', '6', '1')

  await newDatabase(producer, 'mongo-db', '1') // accounts db
  await newDatabase(producer, 'mongo-db', '2') // pings db
  await newDatabase(producer, 'mongo-db', '3') // trends db
  await newDatabase(producer, 'mongo-db', '4') // timelines db
  await newDatabase(producer, 'mongo-db', '5') // Interactions db

  await newDBUsage(producer, '1', '1')
  await newDBUsage(producer, '2', '4')
  await newDBUsage(producer, '3', '4')
  await newDBUsage(producer, '4', '3')
  await newDBUsage(producer, '5', '2')
  await newDBUsage(producer, '6', '5')

  await newOperation(producer, 'POST', '/registration', '1')
  await newOperation(producer, 'DELETE', '/registration/:id', '1')
  await newOperation(producer, 'PATCH', '/registration/:id', '1')
  await newOperation(producer, 'POST', '/login', '1')
  await newOperation(producer, 'PATCH', '/login', '1')
  await newOperation(producer, 'DELETE', '/login', '1')
  await newOperation(producer, 'PUT', '/profiles/:id', '1')
  await newOperation(producer, 'GET', '/profiles/:id', '1')

  await newOperation(producer, 'POST', '/friendships', '2')
  await newOperation(producer, 'PUT', '/friendships/:id', '2')

  await newOperation(producer, 'GET', '/profile/:id/timeline', '3')

  await newOperation(producer, 'GET', '/trends', '4')

  await newOperation(producer, 'GET', '/pings/:id', '5')
  await newOperation(producer, 'POST', '/pings', '5')
  await newOperation(producer, 'DELETE', '/pings/:id', '5')

  await newOperation(producer, 'POST', '/pings/:id/likes', '6')
  await newOperation(producer, 'POST', '/pings/:id/shares', '6')
}

manualPopulate()
