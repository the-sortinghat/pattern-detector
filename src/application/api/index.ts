import Express, { NextFunction, Request, Response } from 'express'
import cors from 'cors'

import { ISystemRepository, PatternDetectorService } from '../../domain'
import { ExpressController } from './ExpressController'
import { Logger } from '../logger/Logger'

export function setupHttpApi(
  repo: ISystemRepository,
  detectionService: PatternDetectorService,
  logger: Logger,
): Express.Application {
  function log(req: Request, res: Response, next: NextFunction) {
    const verb = req.method
    const path = req.path
    logger.http(`--> ${verb} ${path}`)

    const start = new Date().getMilliseconds()
    next()
    const end = new Date().getMilliseconds()

    const status = res.statusCode
    logger.http(`<-- ${status} (${end - start} ms)`)
  }

  const ctrl = new ExpressController(repo, detectionService, logger)

  const api = Express()

  api.use(Express.json())
  api.use(cors())

  api.get('/patterns', log, ctrl.launchDetections.bind(ctrl))

  return api
}
