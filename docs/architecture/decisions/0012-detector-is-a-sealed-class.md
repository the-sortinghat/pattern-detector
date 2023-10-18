# 12. Detector is a sealed class

Date: 2023-10-17

## Status

Accepted

## Context

`Detector` is the superclass of all detectors.
Sealed classes have their subclasses available via reflection in compile time.

## Decision

`Detector` is a [sealed class][sealed-doc]

## Consequences

Subclasses of `Detector` become available via reflection.
There can't be a subclass of `Detector` outside its package.
This compromises the ability of mocking behavior for tests.


[sealed-doc]:https://kotlinlang.org/docs/sealed-classes.html