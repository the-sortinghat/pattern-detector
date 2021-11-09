import Express from 'express'
import cors from 'cors'

import { ISystemRepository, PatternDetectorService } from '../../domain'
import { ExpressController } from './ExpressController'

export function setupHttpApi(
  repo: ISystemRepository,
  detectionService: PatternDetectorService,
): Express.Application {
  const ctrl = new ExpressController(repo, detectionService)

  const api = Express()

  api.use(Express.json())
  api.use(cors())

  api.get('/patterns', ctrl.launchDetections.bind(ctrl))

  return api
}
