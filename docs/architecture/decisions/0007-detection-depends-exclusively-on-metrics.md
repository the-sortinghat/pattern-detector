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
