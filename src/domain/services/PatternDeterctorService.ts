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

    system.accept(metricCollector)
    const dbpsDetector = DatabasePerServiceDetector.create(metricCollector.metrics)
    system.accept(dbpsDetector)

    const dbps = dbpsDetector.results
    if (dbps) results = results.concat(dbps as IDatabasePerServiceResult[])

    return results
  }
}
