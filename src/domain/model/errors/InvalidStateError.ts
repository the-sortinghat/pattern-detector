export class InvalidStateError extends Error {
  constructor(msg: string) {
    super(`Invalid state: ${msg}`)
    Object.setPrototypeOf(this, InvalidStateError.prototype)
  }
}
