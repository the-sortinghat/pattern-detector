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

  await newSystem(producer, 'Fakr', '1')

  await newService(producer, 'Service 1', '1', '1')
  await newService(producer, 'Service 2', '2', '1')
  await newService(producer, 'Service 3', '3', '1')
  await newService(producer, 'Service 4', '4', '1')

  await newDatabase(producer, 'FakeDB', '1')
  await newDatabase(producer, 'FakeDB', '2')
  await newDatabase(producer, 'FakeDB', '3')

  await newOperation(producer, 'GET', '/path1', '1')
  await newOperation(producer, 'GET', '/path2', '1')
  await newOperation(producer, 'GET', '/path3', '2')
  await newOperation(producer, 'GET', '/path4', '3')
  await newOperation(producer, 'GET', '/path5', '3')
  await newOperation(producer, 'GET', '/path6', '3')
  await newOperation(producer, 'GET', '/path7', '3')
  await newOperation(producer, 'GET', '/path8', '3')
  await newOperation(producer, 'GET', '/path9', '3')
  await newOperation(producer, 'GET', '/path10', '3')
  await newOperation(producer, 'GET', '/path11', '3')
  await newOperation(producer, 'GET', '/path12', '3')
  await newOperation(producer, 'GET', '/path13', '3')
  await newOperation(producer, 'GET', '/path14', '3')
  await newOperation(producer, 'GET', '/path15', '3')
  await newOperation(producer, 'GET', '/path16', '3')
  await newOperation(producer, 'GET', '/path17', '3')
  await newOperation(producer, 'GET', '/path18', '3')
  await newOperation(producer, 'GET', '/path19', '3')
  await newOperation(producer, 'GET', '/path20', '3')
  await newOperation(producer, 'GET', '/path21', '4')

  await newDBUsage(producer, '1', '1')
  await newDBUsage(producer, '2', '2')
  await newDBUsage(producer, '3', '3')
  await newDBUsage(producer, '4', '2')
}

manualPopulate()
