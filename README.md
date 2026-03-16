# Mob Spawn Control

Lightweight Paper plugin for managing mob spawning rules per world on a Minecraft server.

## Requirements

- **Paper** 1.21+ (or compatible forks)
- Java 21

## Project Structure

```
src/main/java/ru/nyansus/mc/mob_spawn_control/
├── MobSpawnToggle.java          # Plugin entry point
├── MobSpawnCommand.java         # Command dispatcher
├── SpawnListener.java           # CreatureSpawnEvent handler
├── SpawnRuleManager.java        # Spawn rule management
├── MobSpawnHelpTopic.java       # Custom /help topic
├── Messages.java                # Locale-aware message loader
├── Permissions.java             # Permission constants
└── command/
    ├── ICommand.java            # Subcommand interface
    ├── ToggleCommand.java       # Base class for enable/disable
    ├── EnableCommand.java       # /mobspawn enable <mob> <world>
    ├── DisableCommand.java      # /mobspawn disable <mob> <world>
    ├── ListCommand.java         # /mobspawn list <world>
    └── ReloadCommand.java       # /mobspawn reload
```

## Building

```bash
./gradlew build
```

The output JAR is placed in `build/libs/`.

## Installation

1. Build the plugin or download the JAR from Releases.
2. Place `mob-spawn-control-<version>.jar` into the server's `plugins/` directory.
3. Restart the server.

## Configuration

Settings are stored in `config.yml`:

- `default-locale` — default language (`en`, `ru`)
- `debug` — enable debug messages when spawn is blocked
- `blocked-spawn-reasons` — spawn reasons subject to rules (`NATURAL`, `SPAWNER`, `SPAWNER_EGG`, etc.)
- `worlds` — per-world spawn rules

Multi-locale support (English, Russian) with automatic client language detection.

## Testing

Tests use **JUnit 4** and [MockBukkit](https://github.com/MockBukkit/MockBukkit).

```bash
./gradlew test
```

Coverage reports (JaCoCo) are generated automatically after tests:

```bash
./gradlew jacocoTestReport
# HTML report: build/reports/jacoco/test/html/index.html
```

## Code Style

The project uses [Checkstyle](https://checkstyle.org/) with a configuration based on Google Java Style (4-space indent, 120-char line length).

```bash
./gradlew checkstyleMain checkstyleTest
```

## CI

GitHub Actions workflow (`.github/workflows/build.yml`) runs on pushes and PRs to `main`/`master`:

1. Checkstyle
2. Build + tests
3. JAR artifact upload

## License

MIT
