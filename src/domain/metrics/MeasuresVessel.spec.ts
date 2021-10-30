import { MeasuresVessel, Metrics } from './MeasuresVessel'

describe(MeasuresVessel, () => {
  let target: MeasuresVessel

  beforeEach(() => {
    target = new MeasuresVessel()
  })

  describe('increment', () => {
    it('works with 1 by default', () => {
      const N = 4
      for (let i = 0; i < N; i++) target.increment(Metrics.nOperations)
      expect(target.nOperations).toEqual(N)
    })

    it('works with an arbitrary step', () => {
      const N = 4,
        step = 3

      for (let i = 0; i < N; i++) target.increment(Metrics.nOperations, step)

      expect(target.nOperations).toEqual(N * step)
    })
  })

  describe('decrement', () => {
    it('works with 1 by default', () => {
      const N = 4
      for (let i = 0; i < N; i++) target.decrement(Metrics.nOperations)
      expect(target.nOperations).toEqual(-1 * N)
    })

    it('works with an arbitrary step', () => {
      const N = 4,
        step = 3

      for (let i = 0; i < N; i++) target.decrement(Metrics.nOperations, step)

      expect(target.nOperations).toEqual(-1 * N * step)
    })
  })
})
