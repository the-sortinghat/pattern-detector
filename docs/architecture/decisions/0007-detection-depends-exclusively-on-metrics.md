# 7. Detection depends exclusively on metrics

Date: 2023-10-16

## Status

Accepted

## Context

The tool follow a metric-based approach.
Analyses can be done on the model in different shapes, such as metrics and structural features.

## Decision

Detections depend exclusively on metric and results of analyses.

## Consequences

Detections are decoupled from the model, supported solely by the results of the analyses.
This requires a broader set of analyses to be available, not only metrics but also structural features.

## Illustration

Sequence diagram and a dependency matrix of the reports generation. This process triggers the analyzers and the detectors.

It's important to notice there's no connection between a Detector and a System. In the diagram, you can notice there's no calls between Detector and System; in the matrix, in the column "Detector" the cell in the row "System" is empty.

![sequence diagram of the generation of reports](./../diagrams/usvision-docs-seq_reports.png)

|                   | Report Supervisor | Planner | Executioner | System | Detector | Analyzer |
|-------------------|-------------------|---------|-------------|--------|----------|---------|
| Report Supervisor | -                 |         |             |        |          |         |
| Planner           | 1                 | -       |             |        |          |         |
| Executioner       | 1                 |         | -           |        |          |         |
| System            |                   |         | 1           | -      |          |         |
| Detector          |                   | 1       | 2           |        | -        |         |
| Analyzer          |                   | 1       |             | 1      | 1        | -       |
