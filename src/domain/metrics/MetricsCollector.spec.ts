import { System } from '../model/System'
import { Service } from '../model/Service'
import { MetricsCollector } from './MetricsCollector'

class MockService extends Service {}

describe(MetricsCollector, () => {
  let collector: MetricsCollector

  beforeEach(() => {
    collector = MetricsCollector.create()
  })

  describe('collectFromSystem', () => {
    let system: System
    let mockedService: MockService

    beforeEach(() => {
      system = System.create('ToBeMocked')
      mockedService = MockService.create('TotalMock')
      mockedService.accept = jest.fn()
      system.addService(mockedService)
      collector.collectFromSystem(system)
    })

    it('visits the services of the system', () => {
      expect(mockedService.accept).toHaveBeenCalledWith(collector)
    })
  })

  describe('collectFromService', () => {})

  describe('collectFromDatabase', () => {})
})
