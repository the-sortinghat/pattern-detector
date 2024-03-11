# 6. Specialize classes for analyzing metrics

Date: 2023-10-16

## Status

Accepted

## Context

A series of metrics are going to be measured in the application.
There are metrics that can be evaluated independently from each other.
There are metrics that might depend on other metrics.

## Decision

Implement each metric as a specific class, that might have an uniform interface.

## Consequences

The number of classes grow at the same rate as new types of metrics are evaluated.
It's easier to reuse existing metrics.
It's possible to evaluate just a subset of the metrics.
