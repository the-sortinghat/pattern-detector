import { ExpressController } from './ExpressController'
import { ISystemRepository, PatternDetectorService, System } from '../../domain'

describe(ExpressController, () => {
  let ctrl: ExpressController
  let mockRepo: ISystemRepository
  let mockService: PatternDetectorService

  beforeEach(() => {
    mockRepo = {
      findOne: jest.fn(),
      save: jest.fn(),
    }
    mockService = { detectInSystem: jest.fn() }
    ctrl = new ExpressController(mockRepo, mockService)
  })

  describe('launchDetections', () => {
    let req: any
    let res: any
    let sys: System

    beforeEach(() => {
      res = {
        status: jest.fn(),
        json: jest.fn(),
      }
    })

    describe('when a system_id is not provided', () => {
      beforeEach(async () => {
        req = { query: {} }
        await ctrl.launchDetections(req, res)
      })

      it('does not get to call SystemRepository.findOne', () => {
        expect(mockRepo.findOne).not.toHaveBeenCalled()
      })

      it('does not get to call PatternDetectorService.detectInSystem', () => {
        expect(mockService.detectInSystem).not.toHaveBeenCalled()
      })

      it('responds with 400', () => {
        expect(res.status).toHaveBeenCalledWith(400)
      })

      it('responds with an error informing missing parameter', () => {
        expect(res.json).toHaveBeenCalledWith({
          error: 'system_id must be provided via query string',
        })
      })
    })

    describe('when a system_id is provided', () => {
      beforeEach(() => {
        req = { query: { system_id: 'abcde' } }
      })

      describe('when the system is found', () => {
        beforeEach(async () => {
          sys = System.create('mocked system', req.query.system_id)
          // @ts-expect-error
          mockRepo.findOne.mockResolvedValueOnce(sys)
          // @ts-expect-error
          mockService.detectInSystem.mockReturnValueOnce([])

          await ctrl.launchDetections(req, res)
        })

        it('responds with 200', () => {
          expect(res.status).toHaveBeenCalledWith(200)
        })

        it('sends a list of patterns', () => {
          expect(res.json).toHaveBeenCalledWith({ system_id: req.query.system_id, patterns: [] })
        })
      })

      describe('when the system is not found', () => {
        let throwMessage: string

        beforeEach(async () => {
          throwMessage = `not found - System ${req.query.system_id}`
          // @ts-expect-error
          mockRepo.findOne.mockRejectedValueOnce(throwMessage)
          // @ts-expect-error
          mockService.detectInSystem.mockReturnValueOnce([])

          await ctrl.launchDetections(req, res)
        })

        it('responds with 404', () => {
          expect(res.status).toHaveBeenCalledWith(404)
        })

        it('sends the error message', () => {
          expect(res.json).toHaveBeenCalledWith({ error: throwMessage })
        })
      })
    })
  })
})
