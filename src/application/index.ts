import { setupPersistence } from './database'
import { setupReactiveApp } from './reactive'
import { setupHttpApi } from './api'
import { PatternDetectorService } from '../domain'
import { Logger } from './logger/Logger'

export async function PatternDetectorApplication(): Promise<void> {
  const logger = new Logger()

  const {
    systemRepository: repo,
    systemDAO: sysDao,
    databaseDAO: dbDao,
  } = await setupPersistence('pattern-detector', logger)

  setupReactiveApp(repo, sysDao, dbDao, logger)

  const port = process.env.PORT || 3000
  const detectionService = new PatternDetectorService()
  const api = setupHttpApi(repo, detectionService, logger)

  api.listen(port, () => logger.info(`API listening on :${port}`))
}
