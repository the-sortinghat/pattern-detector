import { Consumer, KafkaClient, Producer } from 'kafka-node'

export class Kafka {
  private _client: KafkaClient

  constructor() {
    const kafkaHost = process.env.KAFKA_HOST as string
    this._client = new KafkaClient({ kafkaHost })
  }

  public createProducer(): Producer {
    return new Producer(this._client)
  }

  public createConsumer(topic: string): Consumer {
    return new Consumer(this._client, [{ topic, partition: 0 }], {})
  }
}
