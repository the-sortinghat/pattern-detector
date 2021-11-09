import { setupPersistence } from './database'
import { setupReactiveApp } from './reactive'
import { setupHttpApi } from './api'
import { PatternDetectorService } from '../domain'

export async function PatternDetectorApplication(): Promise<void> {
  const {
    systemRepository: repo,
    systemDAO: sysDao,
    databaseDAO: dbDao,
  } = await setupPersistence('pattern-detector')

  setupReactiveApp(repo, sysDao, dbDao)

  const port = process.env.PORT || 3000
  const detectionService = new PatternDetectorService()
  const api = setupHttpApi(repo, detectionService)

  api.listen(port, () => console.log(`API listening on :${port}`))
}
