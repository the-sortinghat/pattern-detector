# 10. User can specify a preset

Date: 2023-10-17

## Status

Accepted

Improves [9. Detections can be required in a granular way](0009-detections-can-be-required-in-a-granular-way.md)

Improved by [11. Presets treated as configuration instead of code](0011-presets-treated-as-configuration-instead-of-code.md)

## Context

With multiple possibilities, it can become hard -- or boring -- to select the detections to run.

## Decision

User can specify a preset of detections.

## Consequences

Users need to have access to the currently available presets.
Supporting presets requires pre-processing requests.

## Creating a new preset

The `ReportSupervisor` has a map that translates a preset name into a set of detector names. To create a new preset, all it takes is to add a new record to this map.
