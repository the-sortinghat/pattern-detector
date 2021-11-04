import {
  DatabasePerServiceDetector,
  IDatabasePerServiceResult,
} from '../metrics/DatabasePerServiceDetector'
import { MetricsCollector } from '../metrics/MetricsCollector'
import { System } from '../model/System'

export type PatternDetectionResult = IDatabasePerServiceResult

export class PatternDetectorService {
  public detectInSystem(system: System): PatternDetectionResult[] {
    let results: PatternDetectionResult[] = []

    const metricCollector = MetricsCollector.create()
    const dbpsDetector = DatabasePerServiceDetector.create()

    system.accept(metricCollector)
    system.accept(dbpsDetector)

    const dbps = dbpsDetector.results
    if (dbps) results = results.concat(dbps as IDatabasePerServiceResult[])

    return results
  }
}
