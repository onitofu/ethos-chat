# Ethos Chat

Ethos Chat is a Paper plugin for formatted chat, role-play identities, AFK state, tab-list layout, and floating
nametags on the Ethos server.

## Features

- MiniMessage chat formatting with deterministic player colors and optional gradients
- Per-player RP names, races, and nametag height offsets
- Local chat with a configurable radius and a prefix for global messages
- Manual and automatic AFK state with server-wide away and return announcements
- Persistent RP activity status and RP-first tab-list sorting
- Configurable tab columns for titles, karma, ping, and RP state
- English and Russian messages
- Optional PlaceholderAPI integration with Ethos and other plugins

## Requirements

- Java 21
- Paper 1.21.11
- PlaceholderAPI 2.11.6 or newer (optional)

## Installation

1. Download `ethos-chat-1.0.0.jar`.
2. Place it in the server's `plugins/` directory.
3. Restart the server.
4. Edit `plugins/ethos-chat/config.yml` and run `/ethoschat reload`.

PlaceholderAPI is optional. Without it, configured title and karma placeholders resolve to empty values.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/afk` | `ethos.chat.afk` | Toggle your AFK state |
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

- `config.yml` controls chat formatting, local chat, colors, ping thresholds, tab layout, AFK timeout, and nametags.
- `players.yml` stores explicit player colors.
- `rpnames.yml` stores RP names, races, activity state, and nametag offsets by player UUID.
- `messages_en.yml` and `messages_ru.yml` contain localized messages.

Legacy `tab-colors` and `tab-update-interval` settings remain supported when upgrading an existing installation.

## Building and testing

```bash
./gradlew check
./gradlew build
```

The release JAR is written to `build/libs/ethos-chat-1.0.0.jar`. Tests use JUnit 4 and MockBukkit; JaCoCo reports are
generated in `build/reports/jacoco/test/html/`.

## License

Copyright (c) 2026 Nyansus. Released under the [MIT License](LICENSE).
