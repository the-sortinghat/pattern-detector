import { System } from 'domain/model/System'
import { Service } from 'domain/model/Service'
import { Operation } from 'domain/model/Operation'
import { Database } from 'domain/model/Database'
import { DatabaseUsage } from 'domain/model/DatabaseUsage'
import { IVisitor } from '../utils/visitor.interface'

export class PatternDetector implements IVisitor {
  public static create(): PatternDetector {
    return new PatternDetector()
  }

  private constructor() {}

  public visitSystem(system: System): void {
    system.services.forEach((svc: Service) => svc.accept(this))
  }

  public visitService(svc: Service): void {
    svc.usages.forEach((usage: DatabaseUsage) => usage.accept(this))
  }

  public visitOperation(op: Operation): void {}

  public visitDatabase(db: Database): void {}

  public visitDatabaseUsage(usage: DatabaseUsage): void {}
}
