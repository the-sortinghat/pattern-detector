import { System } from '../model/System'
import { Service } from '../model/Service'
import { Operation } from '../model/Operation'
import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { IVisitor } from '../utils/Visitor.interface'

export class DatabasePerServiceDetector implements IVisitor {
  public static readonly maxOperationsThreshold = 10

  public static create(): DatabasePerServiceDetector {
    return new DatabasePerServiceDetector()
  }

  private svcCandidates: Service[]
  private dbCandidates: Database[]

  private constructor() {
    this.svcCandidates = []
    this.dbCandidates = []
  }

  public visitSystem(system: System): void {
    system.services.forEach((svc: Service) => svc.accept(this))
  }

  public visitService(svc: Service): void {
    const singleUsage = svc.measuresVessel.nDatabaseUsing === 1
    const hasFewOperations =
      svc.measuresVessel.nOperations <= DatabasePerServiceDetector.maxOperationsThreshold

    if (singleUsage && hasFewOperations) this.addCandidate(svc)

    svc.usages.forEach((usage: DatabaseUsage) => usage.accept(this))
  }

  public visitOperation(op: Operation): void {}

  public visitDatabase(db: Database): void {
    const singleClient = db.measuresVessel.nUsageClients === 1

    if (singleClient) this.addCandidate(db)
  }

  public visitDatabaseUsage(usage: DatabaseUsage): void {
    usage.ofDatabase.accept(this)
  }

  public addCandidate(candidate: Service | Database): void {
    if (candidate instanceof Service) this.svcCandidates.push(candidate)
    else if (candidate instanceof Database) this.dbCandidates.push(candidate)
  }
}
