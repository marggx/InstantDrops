# Instant Drops

NPCs drop their loot **the moment they die** instead of waiting for the corpse to disappear.

---

## Features

- Loot spawns at the NPC's position on the tick of death
- Works with drop lists and items an NPC picked up (pickup drop on death)
- Respects the game's death config: only drops when the item loss mode is `ALL`
- Never double-drops: the vanilla corpse-timed drop is disabled for that NPC once the loot is out
- No configuration, no commands, no assets. Drop it in and it works

## Compatibility

- Hytale `release` 0.7.x (built and tested against 0.7.7)

## Installation

1. Download the latest `InstantDrops-<version>.jar`
2. Put it in your server's `mods` folder
3. Restart the server

## Building

```
./gradlew build
```

The jar ends up in `build/libs/`. The build compiles against `HytaleServer.jar` from your Hytale install; set `patchline` in `gradle.properties` (default `release`) or pass `-Phytale_home=<path>` for a custom install location.

---

# Contributing

Suggestions, ideas, and feedback are welcome.

---

# Socials

### X: [@MarggxDev](https://l.marggx.dev/x)

### Discord: [Marggx](https://l.marggx.dev/discord)

### Reddit: [Marggx](https://l.marggx.dev/reddit)

### YouTube: [Marggx](https://l.marggx.dev/yt)

### Patreon: [Marggx](https://l.marggx.dev/patreon)
