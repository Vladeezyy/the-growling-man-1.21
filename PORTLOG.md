# Port log — The Growling Man 1.0.1 (Forge 1.20.1) → NeoForge and Fabric 1.21.1

Original: [The Growling Man](https://www.curseforge.com/minecraft/mc-mods/the-growling-man) 1.0.1 by Voxla (directed by
Lord0wnage, creature by Bonesworth), MIT, made with MCreator. There is no public source: the port starts from the released
jar `growling_man-1.0.1-1.20.1.jar` (sha256 `814fdf1a8e6516618e3d0900a70a1e1f6d1b60514d5edb27c8f6fc6205a4fb28`).

## Method (2026-11-01)
- Commit 1: the jar decompiled with Vineflower 1.11.2, SRG member names (`m_12345_`, `f_12345_`) mapped to Mojang names
  with the 1.20.1 `intermediateToNamed.srg` of MDG; resources unchanged. Commit 2: one decompilation error fixed by the
  bytecode (the config spec was built before its values).
- Build and port layer from the Midnight Lurker port (same author, MCreator): `common` (the mod on `port.bus` events and
  `port.platform`), `neoforge`, `fabric`.
- Java: `tools/port_all.sh` = `port_decompiled.py` (Vineflower output → the shape of MCreator's source: merged scopes,
  3-space indents, nested imports, capability lambdas), the shared `port_java.py` (Forge 1.20.1 → NeoForge 1.21.1) and
  `port_multiloader.py` (NeoForge → `port.bus`), `port_fixups.py` (this mod), `port_perf.py` (searches, below).
- Checks (all through the hang watchdog `tools/watchdog.sh`):
  - `tools/servertest.sh`: the dev server starts on a fresh world.
  - `tools/scenetest.sh` (`port/DevScene`, 9 checks): the Growling Man renders, the HUD overlays, the mark in the
    inventory, the clear mind key reaches the server, the config file, translations.
  - `tools/nettest.sh` (`port/NetScene`): a dedicated server and a client that joins it, survival night, marked, inventory
    open, a Growling Man next to it for 75 s; NeoForge also checks that every payload channel is negotiated.
  - `tools/perf.sh` (`port/PerfScene`): MSPT, FPS, sync packets and the mod's own time, windows with the mod's listeners
    on and off in the same world.

## API changes and how they are handled
- Player variables: Forge capability → `Platform` attachment (NeoForge data attachment, Fabric attachment), synced with a
  payload; world / map variables are SavedData with the 1.21 factory. Same fields, names and copy-on-death rules.
- The clear mind key: SimpleChannel message → serverbound payload; the key mapping, level tick and FOV events are on
  `port.bus` (Fabric: Fabric API events, `GameRenderer.getFov` mixin).
- `growling_man_config.toml`: the same file, keys, comments and defaults. NeoForge registers a `ModConfigSpec` (COMMON, so
  Configured edits it); Fabric reads and writes the same TOML (`port/TomlConfigFile`) and reloads it when it changes.
- The entity: `getDefaultDimensions` (LivingEntity.getDimensions is final), the animation sync of the LivingTickEvent
  listener is in the entity's own `tick()` (no listener for every living entity of the world).
- The see-through render type is created once per texture (1.0.1 created a new one every frame).
- The regen block entity: 1.21 load / save with registries. 1.0.1 exposed its 0 slots to pipes; that is dropped.

## Fixed bugs of the original
- **Dedicated server crash:** the player tick on the server read `Minecraft.getInstance().screen` (is the inventory open?).
  On a dedicated server that class does not exist (`NoClassDefFoundError`), in a LAN world every player was checked
  against the host's screen. The client now tells the server when its inventory opens or closes (`InventoryScreenMessage`).
- **Timers 3× too fast:** the spawn timer and the grace timer ran after the tick of every dimension (overworld, nether,
  end), so the Growling Man came three times as often as the seconds of the config say. They now count once per tick.
  For the old pace set `GM Spawntime Min` / `Max` to a third (20 / 80).

## Performance
Measured with `tools/perf.sh` (singleplayer, survival night, marked, a Growling Man hunting, 40 cows; µs per tick):

| | 1.0.1 code (port before) | now NeoForge | now Fabric |
|---|---|---|---|
| Growling Man's tick, server | 579 | 179 | 157 |
| Growling Man's tick, client | 479 | 76 | 81 |
| player tick listeners, server (one player) | 26 | 31 | 22 |
| sync packets per second | 220 | 40 | 40 |
| MSPT with the mod / listeners off | 3.00 / 2.48 | 2.81 / 2.57 | 2.43 / 2.16 |

- `PlayerCanSeeGM` (80% of the mod's server time): for every player within 160 blocks, 160 rays of growing length per
  tick, each cast three times. Now one ray and the same 160 points along it (`GMEntities.growlingManInView`).
- 53 searches `getEntitiesOfClass(Player.class, box)` in boxes up to 600 blocks: the level's player list with the same
  bounding box test (`GMEntities.players`). 25 loops over every entity within 75–160 blocks whose body acts on players or
  Growling Men only: those are searched.
- MCreator syncs the variables after every write (several times per tick per player, every tick for the timers, to every
  player): one sync per tick with the latest values.
