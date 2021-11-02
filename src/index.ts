import { Message } from 'kafka-node'
import { Kafka } from './reactive/Kafka'

const kafka = Kafka.inst

const consumer = kafka.createConsumer('hackathon')
const producer = kafka.createProducer()

consumer.on('message', (msg: Message) => {
  console.log(JSON.parse(msg.value as string))
})

consumer.on('error', (err) => console.log(err))

function newMessage(topic: string, payload: Record<string, any>) {
  return {
    topic,
    messages: JSON.stringify(payload),
  }
}

const messages = [
  newMessage('hackathon', {
    name: 'padathon',
    patrocinador: 'leite moça',
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
