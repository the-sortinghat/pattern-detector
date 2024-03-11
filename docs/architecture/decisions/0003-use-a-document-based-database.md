# 3. Use a Document-based database

Date: 2023-10-16

## Status

Accepted

## Context

SQL databases are support transactions and are less space-consuming.
Document-modelled NoSQL DBMSs usually support very performant reading operations -- at the expense of data replication.

## Decision

Use MongoDB (Document-based database) as BDMS.

## Consequences

Performant reading operations.
Relies on data replication, so consumes more space as a resource.
It's harder to support transactions.
