import { System } from '../model/System'

export interface ISystemRepository {
  save: (system: System) => Promise<System>
  update: (sID: string, updated: System) => Promise<System>
  findOne: (sID: string) => Promise<System>
}
