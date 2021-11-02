import { KafkaController } from './reactive/KafkaController'
import { Kafka } from './reactive/Kafka'

const kafka = Kafka.inst

const consumer = kafka.createConsumer('hackathon')
const producer = kafka.createProducer()

const kafkaCtrl = new KafkaController()

consumer.on('message', kafkaCtrl.printMessage.bind(kafkaCtrl))

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
