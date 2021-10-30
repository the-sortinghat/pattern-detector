import { Service } from 'domain/model/Service'
import { System } from 'domain/model/System'

export class MetricsCollector {
  static create(): MetricsCollector {
    return new MetricsCollector()
  }

  protected constructor() {}

  collectFromSystem(system: System): void {
    // @ts-ignore
    system.services.forEach((svc: Service) => svc.accept(this))
  }
}
