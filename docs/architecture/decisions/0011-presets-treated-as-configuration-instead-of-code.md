# 11. Presets treated as configuration instead of code

Date: 2023-10-17

## Status

Accepted

Improves [10. User can specify a preset](0010-user-can-specify-a-preset.md)

## Context

Presets combine a set of detectors to be helpful for the users.
As a simple combination of detectors, there's no need to recompile the code to add new presets.

## Decision

Presets are treated as configuration, instead of being defined in code.

## Consequences

It's easier to change the presets without recompiling the code.
On startup, it requires pre-processing of configuration before presets are available to use.


## How to define a new preset

In the `app-web` module, open the `src/main/resources/application.yaml` file. There will be a configuration rooted at `reports.presets`. Each preset must be mappable to a `Map<String,Set<String>>`, e.g.

```yaml
reports:
  presets:
    "preset name might have whitespace":
      - DetectorOne
      - DetectorTwo
    two:
      - DetectorTwo
```

> :warning: The default preset comprises all the detectors, and it's name is defined in the configuration `reports.default_preset_name`