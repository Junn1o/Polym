# Multi-version build (Stonecutter)

One codebase builds every supported Minecraft version. Source is written for the
active version (`1.21.11`, set in `stonecutter.gradle`); other versions are
generated from it at build time.

## Supported versions

| MC | Per-version settings |
|---|---|
| 1.21.4 / 1.21.5 / 1.21.6 / 1.21.8 / 1.21.11 | `versions/<mc>/gradle.properties` (`mc_compat`, `fabric_version`) |

Shared settings (`mod_version`, `loader_version`, ...) stay in the root `gradle.properties`.

## Commands

| Task | Command |
|---|---|
| Build every version, jars into `build/libs/<mod_version>/` | `./gradlew buildAndCollect` |
| Build one version | `./gradlew :1.21.8:build` |
| Run client of one version | `./gradlew :1.21.8:runClient` |
| Switch the active version (rewrites `src/`, IDE then uses it) | `./gradlew "Set active project to 1.21.8"` |
| Back to the main version before committing | `./gradlew "Reset active project"` |

Always commit with the active version back on `1.21.11`.

## Writing version-specific code

```java
//? if >=1.21.9 {
newApiCall();
//?} else
/*oldApiCall();*/
```

Multi-line else block:

```java
//? if >=1.21.6 {
pose.pushMatrix();
//?} else {
/*pose.pushPose();
pose.translate(x, y, 0);
*///?}
```

Simple renames are handled globally in `stonecutter.gradle` (`replacements`),
e.g. `ResourceLocation` -> `Identifier` for 1.21.11.

GUI calls that differ between versions live in
`src/client/java/com/junnio/polymclient/screen/GuiCompat.java`; prefer adding a
method there over scattering conditions in screens.

## Adding a new Minecraft version

1. Add it to `versions(...)` in `settings.gradle`.
2. Create `versions/<mc>/gradle.properties` with `mc_compat` and `fabric_version`.
3. `./gradlew :<mc>:compileClientJava` and fix errors with `//? if` blocks.

## Automated tests

Tests live in `src/gametest` (own mod `polymcoin-test`, never shipped in the release jar).

| Test | Command | What it checks |
|---|---|---|
| Server GameTest, all versions | `./gradlew runGameTest` | Polym Table crafting: polym recipe, guild gate, vanilla recipe, ingredient consume, remainder items |
| Client GameTest, all versions | `./gradlew --no-parallel runClientGameTest` | Real client boots (mixins apply), Polym Table / Shop / Seller screens open, coin dropped in world |
| One version only | `./gradlew :1.21.4:runClientGameTest` | |

The build fails if any test fails. Client test screenshots are written to
`versions/<mc>/build/run/clientGameTest/screenshots/` for a quick visual check.
