# Ethos Chat

Ethos Chat is the focused runtime for Ethos role-play identities and floating nametags. Its optional
chat and tab-list renderers automatically stay disabled when a specialized owner such as EssentialsXChat, CarbonChat,
or TAB is present.

## Features

- Per-player RP names, races, and nametag height offsets
- Persistent RP activity status
- Floating two-line TextDisplay nametags with configurable height
- PlaceholderAPI values for chat, tab-list, scoreboards, and other plugins
- Optional MiniMessage chat renderer with local/global channels when no external chat owner is installed
- Optional tab-list renderer when TAB is not installed
- English and Russian messages

## Requirements

- Java 21
- Paper 1.21.11
- PlaceholderAPI 2.11.6 or newer (optional)
- EssentialsX/EssentialsXChat, CarbonChat, and TAB are detected automatically when installed

## Installation

1. Download `ethos-chat-1.0.0.jar`.
2. Place it in the server's `plugins/` directory.
3. Restart the server.
4. Edit `plugins/ethos-chat/config.yml` and run `/ethoschat reload`.

PlaceholderAPI is optional. Without it, configured title and karma placeholders resolve to empty values. AFK is
intentionally not implemented here; EssentialsX or another presence plugin must own AFK state.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/rp` | `ethos.chat.rp` | Toggle your persistent RP activity state |
| `/realname <rp-name>` | None | Find an online player's Minecraft name from their RP name |
| `/chatcolor <player> <color\|reset>` | `ethos.chat.color` | Set a player's chat color |
| `/rpname <player> <name\|reset>` | `ethos.chat.rpname` | Set a player's RP name |
| `/rprace <player> <race\|reset>` | `ethos.chat.rpname` | Set a player's RP race |
| `/nametagheight <player> <offset\|reset>` | `ethos.chat.rpname` | Set a player's nametag height offset |
| `/ethoschat reload` | `ethos.chat.admin` | Reload the plugin configuration |

Operators receive the administrative permissions by default. Access to chat colors and RP identity commands can also
be opened to all players through `chatcolor-access` and `rpname-access` in `config.yml`.

## PlaceholderAPI

Ethos Chat consumes the configurable title and karma placeholders in `config.yml`. It also provides:

- `%ethoschat_rpname%`
- `%ethoschat_display_name%`
- `%ethoschat_race%`
- `%ethoschat_has_rpname%`
- `%ethoschat_has_race%`

## Configuration and data

- `config.yml` controls feature ownership, optional chat formatting, colors, ping thresholds, tab layout, and nametags.
- `players.yml` stores explicit player colors.
- `rpnames.yml` stores RP names, races, activity state, and nametag offsets by player UUID.
- `lang/en.yml` and `lang/ru.yml` contain editable MiniMessage localizations.

`ownership.chat`, `ownership.tab-list`, and `ownership.nametags` accept `auto`, `internal`, or `external`. In `auto`
mode the internal chat renderer is disabled when EssentialsXChat, CarbonChat, or ChatControlRed is active; TAB owns
both the player list and nametags when installed. RP identity remains available through PlaceholderAPI for those
external renderers. Ownership changes require a server restart; ordinary formatting changes support reload.

## Building and testing

```bash
./gradlew check
./gradlew build
```

The release JAR is written to `build/libs/ethos-chat-1.0.0.jar`. Tests use JUnit 4 and MockBukkit; JaCoCo reports are
generated in `build/reports/jacoco/test/html/`.

## License

Copyright (c) 2026 Nyansus. Released under the [MIT License](LICENSE).
