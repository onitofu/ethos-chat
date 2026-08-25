# Changelog

## 1.1.0

### New

- `/afk` with automatic idle detection and server-wide away/return announcements
- Persistent `/rp` status with a red or green indicator in Tab
- RP-first player sorting in Tab
- Configurable Tab columns and AFK timeout

### Fixed

- Pixel-accurate Tab alignment for names with nearly identical widths
- Titles can be hidden from Tab without removing them from chat

## 1.0

### New

- Command `/mobspawn enable <mob> <world>` — allow a mob type to spawn
- Command `/mobspawn disable <mob> <world>` — prevent a mob type from spawning
- Command `/mobspawn list <world>` — show spawn rules for a world
- Command `/mobspawn reload` — reload configuration
- Configurable blocked spawn reasons (`NATURAL`, `SPAWNER`, `SPAWNER_EGG`, etc.)
- Per-world spawn rules saved to `config.yml`
- Multi-locale support (English, Russian)
- Debug mode for logging blocked spawns
- Permissions: `mobspawn.admin`, `mobspawn.enable`, `mobspawn.disable`, `mobspawn.list`, `mobspawn.reload`
