# The-Aglomeration

A Mindustry mod focused on new systems and, eventually, a new planet.

## Current prototype: ZDCDC

- Place connected **ZDCDC Construction Zone** blocks.
- Connected blocks are scanned as one zone.
- The zone must form a complete rectangle before construction options are offered.
- House sizes from 1x1 through 8x8 are filtered to fit the zone.
- Selecting a house checks and consumes copper and lead, then tracks construction progress.
- Completed houses currently use the vanilla copper-wall sprite as a visual placeholder.

## Building

This is a Java mod project. Open it with JDK 17 and Gradle, then run:

```sh
./gradlew build
```

The game dependency is set to Mindustry v146 for the initial compatibility target. The house art is temporary and can be replaced later.
