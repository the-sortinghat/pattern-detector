import { Logger } from '../logger/Logger'
import { Request, Response } from 'express'
import { ISystemRepository, PatternDetectorService } from '../../domain'

export class ExpressController {
  constructor(
    private readonly repo: ISystemRepository,
    private readonly detectionService: PatternDetectorService,
    private readonly logger: Logger,
  ) {}

  public async launchDetections(req: Request, res: Response): Promise<void> {
    const { system_id: sID } = req.query

    if (!this.validParameter(sID)) {
      res.status(400)
      res.json({ error: 'system_id must be provided via query string' })
      return
    }

    try {
      const system = await this.repo.findOne(sID as string)
      const patterns = this.detectionService.detectInSystem(system)

      res.status(200)
      res.json({ system_id: sID, patterns })
    } catch (e) {
      res.status(404)
      res.json({ error: e })
    }
  }

  private validParameter(sID: any): boolean {
    if (!sID) return false

    const isString = sID instanceof String || typeof sID === 'string'

    if (!isString) return false

    return true
  }
}
