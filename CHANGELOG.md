# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-04-26

### Added
- Initial release of StatsHolograms
- Four hologram types: Deaths, Kills, K/D Ratio, and Killstreak
- Beautiful purple-themed hologram design
- Persistent holograms that survive server restarts
- Real-time stat tracking for players
- Top 10 leaderboards with personal stats display
- Customizable colors and formatting via config.yml
- Commands to create, remove, and manage holograms
- Tab completion for all commands
- Auto-save functionality for player stats
- GitHub Actions build workflow
- Comprehensive documentation

### Features
- `/hologram deaths` - Create deaths leaderboard
- `/hologram kills` - Create kills leaderboard
- `/hologram kd` - Create K/D ratio leaderboard
- `/hologram killstreak` - Create killstreak leaderboard
- `/hologram remove <id>` - Remove specific hologram
- `/hologram list` - List all active holograms
- `/hologram removeall` - Remove all holograms
- `/hologram reload` - Reload configuration

### Technical Details
- Built for Paper 1.21.1
- Requires Java 17+
- Uses armor stands for hologram display
- Stores data in YAML format
- Automatic hologram updates based on configurable interval

[1.0.0]: https://github.com/yourusername/StatsHolograms/releases/tag/v1.0.0
