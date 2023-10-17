# 11. Presets treated as configuration instead of code

Date: 2023-10-17

## Status

Proposed

Improves [10. User can specify a preset](0010-user-can-specify-a-preset.md)

## Context

Presets combine a set of detectors to be helpful for the users.
As a simple combination of detectors, there's no need to recompile the code to add new presets.

## Decision

Presets are treated as configuration, instead of being defined in code.

## Consequences

It's easier to change the presets without recompiling the code.
On startup, it requires pre-processing of configuration before presets are available to use.
