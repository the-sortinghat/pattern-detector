import { System } from '../model/System'
import { Database } from '../model/Database'
import { Service } from '../model/Service'
import { DatabasePerServiceDetector } from './DatabasePerServiceDetector'
import { HTTPVerb, Operation } from '../model/Operation'
import { DatabaseUsage } from '../model/DatabaseUsage'
import { MetricsCollector } from './MetricsCollector'

describe('the complete detection process', () => {
  let detector: DatabasePerServiceDetector
  let expectation: any[]
  let collector: MetricsCollector

  beforeEach(() => {
    collector = MetricsCollector.create()
  })

  describe('scenario 1', () => {
    beforeEach(() => {
      const { system, expectation: exp } = scenario1()

      expectation = exp

      system.accept(collector)
      detector = DatabasePerServiceDetector.create(collector.metrics)
      system.accept(detector)
    })

    it('detects DB per Service between svc3 and db2', () => {
      expect(detector.results).toEqual(expectation)
    })
  })

  describe('scenario 2', () => {
    beforeEach(() => {
      const { system, expectation: exp } = scenario2()
      expectation = exp

      system.accept(collector)
      detector = DatabasePerServiceDetector.create(collector.metrics)
      system.accept(detector)
    })

    it('detects DB per Service between svc1 and db1', () => {
      expect(detector.results).toEqual(expectation)
    })
  })

  describe('scenario 3', () => {
    beforeEach(() => {
      const { system, expectation: exp } = scenario3()
      expectation = exp

      system.accept(collector)
      detector = DatabasePerServiceDetector.create(collector.metrics)
      system.accept(detector)
    })

    it('detects no DB per Service instances', () => {
      expect(detector.results).toEqual(expectation)
    })
  })
})

interface IScenario {
  system: System
  expectation: any[]
}

function scenario1(): IScenario {
  const system = System.create('toy')

  const svc1 = Service.create('svc1')
  const svc2 = Service.create('svc2')
  const svc3 = Service.create('svc3')

  const op1 = Operation.create(HTTPVerb.GET, '/foo/1')
  const op2 = Operation.create(HTTPVerb.GET, '/foo/2')
  const op3 = Operation.create(HTTPVerb.GET, '/foo/3')
  const op4 = Operation.create(HTTPVerb.GET, '/foo/4')
  const op5 = Operation.create(HTTPVerb.GET, '/foo/5')
  const op6 = Operation.create(HTTPVerb.GET, '/foo/6')

  const db1 = Database.create('mockdb1')
  const db2 = Database.create('mockdb2')

  system.addService(svc1)
  system.addService(svc2)
  system.addService(svc3)

  svc1.addOperation(op1)
  svc1.addOperation(op2)
  svc2.addOperation(op3)
  svc3.addOperation(op4)
  svc3.addOperation(op5)
  svc3.addOperation(op6)

  DatabaseUsage.create(svc1, db1)
  DatabaseUsage.create(svc2, db1)
  DatabaseUsage.create(svc3, db2)

  return { system, expectation: [{ serviceID: svc3.id, databaseID: db2.id }] }
}

function scenario2(): IScenario {
  const sys = System.create('toy2')

  const svc1 = Service.create('svc1')
  const svc2 = Service.create('svc2')
  const svc3 = Service.create('svc3')
  const svc4 = Service.create('svc4')

  sys.addService(svc1)
  sys.addService(svc2)
  sys.addService(svc3)
  sys.addService(svc4)

  const op1 = Operation.create(HTTPVerb.GET, '/path1')
  const op2 = Operation.create(HTTPVerb.GET, '/path2')
  const op3 = Operation.create(HTTPVerb.GET, '/path3')
  const op4 = Operation.create(HTTPVerb.GET, '/path4')
  const op5 = Operation.create(HTTPVerb.GET, '/path5')
  const op6 = Operation.create(HTTPVerb.GET, '/path6')
  const op7 = Operation.create(HTTPVerb.GET, '/path7')
  const op8 = Operation.create(HTTPVerb.GET, '/path8')
  const op9 = Operation.create(HTTPVerb.GET, '/path9')
  const op10 = Operation.create(HTTPVerb.GET, '/path10')
  const op11 = Operation.create(HTTPVerb.GET, '/path11')
  const op12 = Operation.create(HTTPVerb.GET, '/path12')
  const op13 = Operation.create(HTTPVerb.GET, '/path13')
  const op14 = Operation.create(HTTPVerb.GET, '/path14')
  const op15 = Operation.create(HTTPVerb.GET, '/path15')
  const op16 = Operation.create(HTTPVerb.GET, '/path16')

  svc1.addOperation(op1)
  svc1.addOperation(op2)

  svc2.addOperation(op3)

  svc3.addOperation(op4)
  svc3.addOperation(op5)
  svc3.addOperation(op6)
  svc3.addOperation(op7)
  svc3.addOperation(op8)
  svc3.addOperation(op9)
  svc3.addOperation(op10)
  svc3.addOperation(op11)
  svc3.addOperation(op12)
  svc3.addOperation(op13)
  svc3.addOperation(op14)
  svc3.addOperation(op15)

  svc4.addOperation(op16)

  const db1 = Database.create('mockdb')
  const db2 = Database.create('mockdb')
  const db3 = Database.create('mockdb')

  DatabaseUsage.create(svc1, db1)
  DatabaseUsage.create(svc2, db2)
  DatabaseUsage.create(svc3, db3)
  DatabaseUsage.create(svc4, db2)

  return { system: sys, expectation: [{ serviceID: svc1.id, databaseID: db1.id }] }
}

function scenario3(): IScenario {
  const system = System.create('toy 3')

  const svc1 = Service.create('svc1')
  const svc2 = Service.create('svc2')

  system.addService(svc1)
  system.addService(svc2)

  const op1 = Operation.create(HTTPVerb.GET, '/path1')
  const op2 = Operation.create(HTTPVerb.GET, '/path2')

  svc1.addOperation(op1)
  svc2.addOperation(op2)

  const db1 = Database.create('mockdb')

  DatabaseUsage.create(svc1, db1)
  DatabaseUsage.create(svc2, db1)

  return { system, expectation: [] }
}
