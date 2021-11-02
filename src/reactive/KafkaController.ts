import { Message } from 'kafka-node'

export class KafkaController {
  public printMessage(msgString: Message): void {
    const msg = JSON.parse(msgString.value as string)
    console.log(msg)
  }

  public printError(err: any): void {
    console.log(err)
  }
}
