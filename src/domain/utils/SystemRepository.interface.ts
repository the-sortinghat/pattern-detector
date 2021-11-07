import { System } from '../model/System'

export interface ISystemRepository {
  save: (system: System) => Promise<void>
  findOne: (sID: string) => Promise<System>
}
