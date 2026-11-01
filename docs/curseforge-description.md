# The Growling Man (Unofficial Port)

**He stalks you through your world, and he is drawn to sorrow and grief. On NeoForge and Fabric for the new Minecraft versions.**

## 📜 Original mod

This is an unofficial port of **[The Growling Man](https://www.curseforge.com/minecraft/mc-mods/the-growling-man)** 1.0.1
by **Voxla** (directed by Lord0wnage, creature by Bonesworth; Forge 1.20.1). The original has no public source; it is
licensed **MIT** (declared in its mod metadata and on its CurseForge page), which allows derivative works. The port starts
from the released 1.0.1 jar; its source: [github.com/Vladeezyy/the-growling-man-1.21](https://github.com/Vladeezyy/the-growling-man-1.21),
LGPL-3.0, with the MIT text and copyright of the original kept. All the content, art, model, animations, sounds and ideas
are theirs; please report bugs of this port to [this port's issues](https://github.com/Vladeezyy/the-growling-man-1.21/issues),
not to the original authors.

### How this port differs from the original

- **New Minecraft versions and loaders:** NeoForge **and Fabric** for Minecraft **1.21.1, 1.21.11, 26.1.2, 26.2 and 26.3**
  (the original is Forge 1.20.1 and 1.19.2 only).
- **Same content and behaviour:** the Growling Man, his stalking and hunting, the mark, raids, clearing your mind, the
  mental breakdown, the inventory scare, and the same config file (`config/growling_man_config.toml`).
- **Dedicated servers work:** 1.0.1 crashed a dedicated server as soon as a player was ticked (the server read the
  client's screen), and in LAN worlds it checked every player against the host's inventory.
- **The timers follow the config:** in 1.0.1 the spawn timer ran once per dimension, so he came three times as often
  as the configured seconds. For the old pace, set the spawn times to a third (20 / 80).
- **Less lag:** the check whether a player sees him cast 480 rays per player and tick (now one, same result); the
  variables are synced once per tick instead of after every change (220 → 40 packets per second). His own tick:
  ~580 → ~170 µs on the server, ~480 → ~80–100 µs on the client.
- **Tested:** dedicated server, a client scene (the Growling Man, the HUD overlays, the mark in the inventory, the clear
  mind key, the config, translations) and a dedicated server with a client joining it, on every version and loader.

## 👁 About the mod

The Growling Man stalks you through your world. He watches you from caves and the dark, hides when you look at him, and
may appear when you check your inventory. He comes for players who are **marked** — who caused sorrow, such as killing
many animals quickly — and during raids he is overstimulated by all the grief and slaughters everything he sees. When
he stares at you, **clear your mind** (the key, X by default), but not too early, or you have a mental breakdown.

If the music or jumpscare sounds are too loud, turn down the "Jukebox/Note Blocks" volume.

## 📦 Installation

Pick the file for your Minecraft version and loader. Needed on both the client and the server.

- **Minecraft 1.21.1:** NeoForge 21.1.197+ or Fabric (Fabric API), GeckoLib 4.9+
- **Minecraft 1.21.11:** NeoForge 21.11.48+ or Fabric (Fabric API), GeckoLib 5.4.5+
- **Minecraft 26.1.2:** NeoForge 26.1.2.122+ or Fabric (Fabric API), GeckoLib 5.5.2+
- **Minecraft 26.2:** NeoForge 26.2.0.93+ or Fabric (Fabric API), GeckoLib 5.5.6+ (Fabric 5.5.5+)
- **Minecraft 26.3:** NeoForge 26.3.0.97+ or Fabric (Fabric API), GeckoLib 5.5.7+
- **Required:** [GeckoLib](https://www.curseforge.com/minecraft/mc-mods/geckolib) for the same version and loader, and
  [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) on Fabric

## ⚙ Config

`config/growling_man_config.toml`: spawn time min / max (seconds), spawn chance, chase time min / max (minutes),
regenerate blocks broken by him, no spawning on the first day, max spawn distance, quick animal kills needed to get
marked. On NeoForge the [Configured](https://www.curseforge.com/minecraft/mc-mods/configured) mod edits it in game; on
both loaders a changed file is read again while the game runs.

Source, changelog and older files: [github.com/Vladeezyy/the-growling-man-1.21](https://github.com/Vladeezyy/the-growling-man-1.21).
