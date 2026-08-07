# ethos-chat

Paper plugin for Domya SMP chat names, RP names, races, AFK status, tab columns, and nametags.

## Requirements

- **Paper** 1.21+ (or compatible forks)
- Java 21

## Player Commands

- `/afk` toggles AFK. Movement or looking around returns the player; five minutes of inactivity enables it.
- `/rp` toggles the persistent RP indicator in Tab (`red circle | name` or `green circle | name`).
- `/realname <rp-name>` resolves an online player's Minecraft name.

Administrative commands `/chatcolor`, `/rpname`, `/rprace`, `/nametagheight`, and `/ethoschat reload`
retain their existing behavior and permissions.

## Tab Configuration

The `tab` section controls the update interval, title, karma, ping, RP indicator, RP-first sorting, and pixel gap
between columns.
Titles are hidden in Tab by default but remain in chat. Existing `tab-colors` and `tab-update-interval` keys are
accepted for backwards compatibility. `afk.auto-after-seconds` controls the idle timeout; set it to `0` to disable
automatic AFK.

## Building

```bash
./gradlew build
```

The output JAR is placed in `build/libs/`.

## PlaceholderAPI

When PlaceholderAPI is installed, the plugin exposes:

- `%ethoschat_rpname%` — RP name, empty if unset
- `%ethoschat_display_name%` — RP name or Minecraft name fallback
- `%ethoschat_race%` — RP race, empty if unset
- `%ethoschat_has_rpname%` — `true` or `false`
- `%ethoschat_has_race%` — `true` or `false`

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
