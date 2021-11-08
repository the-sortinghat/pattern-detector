import { setupPersistence } from './database'
import { setupReactiveApp } from './reactive'

export async function PatternDetectorApplication(): Promise<void> {
  const {
    systemRepository: repo,
    systemDAO: sysDao,
    databaseDAO: dbDao,
  } = await setupPersistence('pattern-detector')

  setupReactiveApp(repo, sysDao, dbDao)
}
