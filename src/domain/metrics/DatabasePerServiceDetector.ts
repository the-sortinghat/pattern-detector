import { System } from '../model/System'
import { Service } from '../model/Service'
import { Operation } from '../model/Operation'
import { Database } from '../model/Database'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { IVisitor } from '../utils/Visitor.interface'

export interface IDatabasePerServiceResult {
  serviceID: string
  databaseID: string
}

export class DatabasePerServiceDetector implements IVisitor {
  public static readonly maxOperationsThreshold = 10

  public static create(): DatabasePerServiceDetector {
    return new DatabasePerServiceDetector()
  }

  private svcCandidates: Service[]
  private dbCandidates: Database[]
  private _results: IDatabasePerServiceResult[] | undefined

  private constructor() {
    this.svcCandidates = []
    this.dbCandidates = []
    this._results = undefined
  }

  public visitSystem(system: System): void {
    system.services.forEach((svc: Service) => svc.accept(this))

    this.composeResults()
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
    if (candidate instanceof Service) this.svcCandidates.push(candidate as Service)
    else if (candidate instanceof Database) this.dbCandidates.push(candidate as Database)
  }

  public composeResults(): void {
    this._results = []

    while (this.svcCandidates.length > 0) {
      const svc = this.svcCandidates.pop() as Service

      const dbCandidateIndex = this.dbCandidates.findIndex((db: Database) =>
        db.usages.some((usage: DatabaseUsage) => usage.fromService.id === svc.id),
      )

      if (dbCandidateIndex >= 0) {
        const [db] = this.dbCandidates.splice(dbCandidateIndex, 1)
        this._results.push({ serviceID: svc.id, databaseID: db.id })
      }
    }
  }

  public get results(): IDatabasePerServiceResult[] | undefined {
    return this._results
  }
}
