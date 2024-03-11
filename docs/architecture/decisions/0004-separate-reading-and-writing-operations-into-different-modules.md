# 4. Separate reading and writing operations into different modules

Date: 2023-10-16

## Status

Proposed

## Context

Reading and writing operations in this application follow two different workflows.
NFRs for each operation varies.

## Decision

Separate reading and writing operations into different modules of the application.

## Consequences

Maintenance of each can be done independently from each other.
There might be code duplication or replication.
Whenever there's the need to synchronize changes into those modules, it'll be more difficult.
