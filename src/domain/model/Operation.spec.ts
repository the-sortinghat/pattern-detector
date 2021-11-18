import { InvalidStateError } from './errors/InvalidStateError'
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

  describe('create with valid arguments', () => {
    const validUnusualCases = [
      { caseID: `with ':'`, path: '/foo/:id' },
      { caseID: `with '?' and '='`, path: '/foo?key=value' },
    ]

    validUnusualCases.forEach(({ caseID, path }) => {
      test(`${caseID} doesn't throw error`, () => {
        expect(() => Operation.create(HTTPVerb.GET, path)).not.toThrow()
      })
    })
  })

  describe('create with invalid arguments throwing InvalidStateError', () => {
    test('when verb is a string not from the enum', () => {
      // @ts-ignore
      expect(() => Operation.create('foo', '/foo')).toThrowError(InvalidStateError)
    })

    test('when the verb is undefined', () => {
      // @ts-ignore
      expect(() => Operation.create(undefined, '/foo')).toThrowError(InvalidStateError)
    })

    const cases = [
      { id: 'with spaces', path: '/foo /bar' },
      { id: 'without leading forward-slash', path: 'foo/bar' },
      { id: 'with trailling forward-slash', path: '/foo/bar/' },
    ]

    cases.forEach(({ id, path }: any) => {
      test(`when path is not a valid format - case ${id}`, () => {
        // @ts-ignore
        expect(() => Operation.create(HTTPVerb.GET, path)).toThrowError(InvalidStateError)
      })
    })
  })
})
