export enum Metrics {
  nOperations = 'nOperations',
  nDatabaseUsing = 'nDatabaseUsing',
  nUsageClients = 'nUsageClients',
}

export interface MetricsMeasures {
  nOperations?: number
  nDatabaseUsing?: number
  nUsageClients?: number
}

export class MeasuresVessel {
  protected _metrics: MetricsMeasures = {}

  public get metrics(): MetricsMeasures {
    return Object.assign({}, this._metrics)
  }

  increment(metricName: Metrics, n = 1): void {
    if (this._metrics[metricName] === undefined) {
      this._metrics[metricName] = 0
    }

    this._metrics[metricName] = (this._metrics[metricName] as number) + n
  }

  decrement(metricName: Metrics, n = 1): void {
    if (this._metrics[metricName] === undefined) {
      this._metrics[metricName] = 0
    }

    this._metrics[metricName] = (this._metrics[metricName] as number) - n
  }

  public get nOperations(): number {
    return this._metrics.nOperations || 0
  }

  public get nDatabaseUsing(): number {
    return this._metrics.nDatabaseUsing || 0
  }

  public get nUsageClients(): number {
    return this._metrics.nUsageClients || 0
  }
}
