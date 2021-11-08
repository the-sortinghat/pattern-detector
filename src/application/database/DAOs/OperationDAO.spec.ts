import { HTTPVerb, Operation } from '../../../domain'

import { OperationDAO } from './OperationDAO'
import { generateGetOperationDocument, generateGetOperation } from './TestHelpers'

describe(OperationDAO, () => {
  let opDao: OperationDAO

  beforeEach(() => {
    opDao = new OperationDAO()
  })

  describe('docToOperation', () => {
    let op: Operation
    let doc: any

    beforeEach(() => {
      doc = generateGetOperationDocument()
      op = opDao.docToOperation(doc)
    })

    it('returns the right verb', () => {
      expect(op.verb).toEqual(HTTPVerb.GET)
    })
  })

  describe('operationToDoc', () => {
    let op: Operation
    let doc: any

    beforeEach(() => {
      op = generateGetOperation()
      doc = opDao.operationToDoc(op)
    })

    it('returns the right verb string', () => {
      expect(doc.verb).toEqual('GET')
    })
  })
})
