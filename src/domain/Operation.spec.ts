import { HTTPVerb, Operation } from './Operation'

describe(Operation, () => {
  describe('create', () => {
    let operation: Operation

    beforeEach(() => {
      operation = Operation.create(HTTPVerb.GET, '/foo')
    })

    it('returns a Operation instance', () => {
      expect(operation).toBeInstanceOf(Operation)
    })

    it('returns a GET Operation', () => {
      expect(operation.verb).toEqual(HTTPVerb.GET)
    })

    it('returns an Operation over /foo', () => {
      expect(operation.path).toEqual('/foo')
    })

    it('returns a Operation without ID', () => {
      // @ts-ignore
      expect(operation.id).toBeUndefined()
    })
  })
})
