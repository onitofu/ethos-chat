# ethos-chat

Paper plugin for Domya SMP chat names, RP names, races, tab colors, and nametags.

## Requirements

- **Paper** 1.21+ (or compatible forks)
- Java 21

## Project Structure

```
src/main/java/ru/nyansus/mc/ethos_chat/
├── EthosChat.java               # Plugin entry point
├── Messages.java                # Locale-aware message loader
└── command/
    ├── RpNameCommand.java       # /rpname
    ├── RpRaceCommand.java       # /rprace
    └── RealNameCommand.java     # /realname
```

## Building

```bash
./gradlew build
```

The output JAR is placed in `build/libs/`.

## PlaceholderAPI

When PlaceholderAPI is installed, the plugin exposes:

- `%ethos_chat_rpname%` — RP name, empty if unset
- `%ethos_chat_display_name%` — RP name or Minecraft name fallback
- `%ethos_chat_race%` — RP race, empty if unset
- `%ethos_chat_has_rpname%` — `true` or `false`
- `%ethos_chat_has_race%` — `true` or `false`

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
