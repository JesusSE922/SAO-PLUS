# SAO+ Core — Testing Report

Labels: **VERIFIED IN GAME**, **VERIFIED BY CODE**, **NOT VERIFIED**.

## Phase 1 validation — 2026-11-09

Setup: local dedicated server (`runServer`, offline mode, isolated world `phase1_test` created for this test and deleted afterwards) plus one auto-connecting dev client (`DevA`). A temporary dev-only fixture seeded fictitious `saoplus_test:*` mastery data and drove the lifecycle from the server. The fixture, the temporary client log line and the temporary `build.gradle` args were removed afterwards (verified with `git status` and a search for `FIXTURE`/`TEMP`). The user's `New World` and the production server were not touched.

| Check | Result | Evidence |
|---|---|---|
| Capability attached; mastery data seeded and synced on login | VERIFIED IN GAME | Server log `login SEEDED`; client received identical NBT. |
| Death → `PlayerList.respawn` (clone with `wasDeath`) keeps data; client receives sync | VERIFIED IN GAME | `pre-death` and `post-respawn` identical (`experience:777L`, technique kept); client received a packet. |
| Dimension change (Overworld → Nether via `teleportTo`) keeps data; client receives sync | VERIFIED IN GAME | `post-dimension` identical, `dim=the_nether`; client received a packet. |
| Server stop saves data; restart loads it from disk; client receives it | VERIFIED IN GAME | `login LOADED from disk` identical; client received it. |
| Data survives death after a reload (clone of a loaded player) | VERIFIED IN GAME | Diagnostic pass: respawned entity had the full data. |
| No `ERROR` from SAO+ code in the server log | VERIFIED IN GAME | Only Netty reflection debug noise. |
| Weapon Mastery NBT round-trip, tolerance, deep copy, immutability | VERIFIED BY CODE | 9/9 unit tests; `compileJava` + `test` pass. |
| Full `.\gradlew.bat build` | VERIFIED BY CODE | Run 2026-11-09: BUILD SUCCESSFUL, produced `saoplus-1.0.0.jar`; that jar also started on a dedicated server (see `DEPENDENCY_COMPATIBILITY.md`). |
| Two or more simultaneous clients | NOT VERIFIED | Only one client was run. Sync is per-player by design (self only), so cross-player sync is not a requirement. |
| Server authority against forged client data | VERIFIED BY CODE | The channel has one message, `PLAY_TO_CLIENT` only; there is no client → server path. |
| Packet size limit (2 MiB `readNbt`) with large data | NOT VERIFIED | Documented risk only. |
| Behavior with third-party mods | NOT VERIFIED | Phase 2. |

### Observations
- First fixture attempt crashed the server in `changeDimension` (vanilla portal lookup returned a null position). This was a fixture error, not a mod defect; `teleportTo` (the `/tp` path) was used instead.
- After the restart the loaded player was already dead (`alive=false`, `removed=KILLED`); the fixture teleported the player to Nether y=100, likely inside terrain. While that entity was removed the capability was correctly empty (Forge invalidates a removed entity's capabilities), and the respawned entity had the data. This is not an SAO+ defect.
- The `LazyOptional` renewal in `PlayerDataProvider` was not exercised as a problem; its broader semantics (audit finding 3) remain a review item.

## Phase 2 client round — 2026-11-09

Details and evidence are in `DEPENDENCY_COMPATIBILITY.md` (sections 6–9). Summary, isolated Forge 47.4.28 client + dedicated server, 14 candidate mods + `saoplus-1.0.0.jar`:

| Check | Result |
|---|---|
| Client starts; joins dedicated server | VERIFIED IN GAME (1 log ERROR: Epic Fight web server connection) |
| Weapons/equipment placed and read back (Simply Swords, Basic Weapons, vanilla) | VERIFIED IN GAME (server side) |
| World save/load; server restart; client disconnect/reconnect | VERIFIED IN GAME |
| `saoplus:player_data` coexists with `epicfight:skill_cap` and `rpg_attribute_system:player_variables` | VERIFIED IN GAME |
| CompatLink accepts 2 test mappings (0 failed) | VERIFIED IN GAME (log) |
| In-world render with RAS overlay | PARTIALLY VERIFIED (one window screenshot, deleted) |
| Screens/UI, Epic Fight animations, Simply Tooltips (visual), attacks | NOT VERIFIED (no input channel) |
| SAO+ mastery data persistence in the full mod mix | NOT VERIFIED (no mastery data was seeded in this round; `saoplus:player_data` was `{}`) |
| Multiple simultaneous clients | NOT VERIFIED |

Method notes: the launcher was generated from the Forge/vanilla version JSONs; two launcher bugs of mine (vanilla jar on the classpath, fake UUID) were fixed and are not mod defects.
