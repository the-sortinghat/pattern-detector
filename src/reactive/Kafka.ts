import { Consumer, KafkaClient, Producer } from 'kafka-node'

export class Kafka {
  private static _inst: Kafka
  private _client: KafkaClient

  private constructor() {
    const kafkaHost = process.env.KAFKA_HOST as string
    this._client = new KafkaClient({ kafkaHost })
  }

  public static get inst(): Kafka {
    if (!this._inst) this._inst = new Kafka()
    return this._inst
  }

  public createProducer(): Producer {
    return new Producer(this._client)
  }

  public createConsumer(topic: string): Consumer {
    return new Consumer(this._client, [{ topic, partition: 0 }], {})
  }
}
