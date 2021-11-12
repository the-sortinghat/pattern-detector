import {
  DatabasePerServiceDetector,
  IDatabasePerServiceResult,
} from '../metrics/DatabasePerServiceDetector'
import { MetricsCollector } from '../metrics/MetricsCollector'
import { System } from '../model/System'

export interface PatternDetectionResult {
  databasePerService: IDatabasePerServiceResult[]
}

export class PatternDetectorService {
  public detectInSystem(system: System): PatternDetectionResult {
    const results: PatternDetectionResult = {
      databasePerService: [],
    }

    const metricCollector = MetricsCollector.create()

    system.accept(metricCollector)
    const dbpsDetector = DatabasePerServiceDetector.create(metricCollector.metrics)
    system.accept(dbpsDetector)

    const dbps = dbpsDetector.results
    if (dbps) results.databasePerService = dbps

    return results
  }
}
