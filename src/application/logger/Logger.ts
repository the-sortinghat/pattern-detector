import { createLogger, Logger as WLogger, transports as Transports } from 'winston'

export class Logger {
  private readonly _logger: WLogger

  constructor(env = 'development') {
    const level = env === 'development' ? 'debug' : 'info'

    const transports = [new Transports.Console()]

    this._logger = createLogger({ level, transports })
  }

  public error(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.error(msg)
  }

  public warn(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.warn(msg)
  }

  public info(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.info(msg)
  }

  public http(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.http(msg)
  }

  public verbose(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.verbose(msg)
  }

  public debug(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.debug(msg)
  }

  public silly(...args: any[]): void {
    const msg = args.join(' ')
    this._logger.silly(msg)
  }
}
