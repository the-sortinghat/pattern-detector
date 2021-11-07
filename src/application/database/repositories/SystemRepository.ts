import { ISystemRepository } from '../../../domain/utils/SystemRepository.interface'
import { System } from '../../../domain/model/System'
import { ISystemDAO } from '../../utils/SystemDAO.interface'

export class SystemRepository implements ISystemRepository {
  constructor(private readonly systemDAO: ISystemDAO) {}

  public async save(system: System): Promise<void> {
    return await this.systemDAO.store(system)
  }

  public async findOne(id: string): Promise<System> {
    return await this.systemDAO.findOne(id)
  }
}
