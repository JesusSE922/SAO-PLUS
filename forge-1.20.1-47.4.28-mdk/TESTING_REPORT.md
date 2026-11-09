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
| Full `.\gradlew.bat build` | NOT VERIFIED | Not run in this session. |
| Two or more simultaneous clients | NOT VERIFIED | Only one client was run. Sync is per-player by design (self only), so cross-player sync is not a requirement. |
| Server authority against forged client data | VERIFIED BY CODE | The channel has one message, `PLAY_TO_CLIENT` only; there is no client → server path. |
| Packet size limit (2 MiB `readNbt`) with large data | NOT VERIFIED | Documented risk only. |
| Behavior with third-party mods | NOT VERIFIED | Phase 2. |

### Observations
- First fixture attempt crashed the server in `changeDimension` (vanilla portal lookup returned a null position). This was a fixture error, not a mod defect; `teleportTo` (the `/tp` path) was used instead.
- After the restart the loaded player was already dead (`alive=false`, `removed=KILLED`); the fixture teleported the player to Nether y=100, likely inside terrain. While that entity was removed the capability was correctly empty (Forge invalidates a removed entity's capabilities), and the respawned entity had the data. This is not an SAO+ defect.
- The `LazyOptional` renewal in `PlayerDataProvider` was not exercised as a problem; its broader semantics (audit finding 3) remain a review item.
