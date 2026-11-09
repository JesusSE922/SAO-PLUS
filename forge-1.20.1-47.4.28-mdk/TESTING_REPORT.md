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

## Final integration campaign — 2026-11-09 (SAO+ Core with services, specialization and family catalog)

Method: temporary dev-only fixture (removed afterwards; verified by search and by the jar contents) drove a real dedicated server + one auto-connecting client, in two scenarios: **A** Core + the 14 candidate mods, **B** Core alone. Datapack with fictitious `saoplus_test` families (item `minecraft:iron_sword` → family_a, tag `minecraft:axes` → family_b). Each scenario: first pass (fresh world) and a second pass after server restart.

| Check | A: 14 mods + Core | B: Core alone |
|---|---|---|
| World creation, join, leave | PASSED | PASSED |
| Datapack catalog loaded (`Loaded 2 weapon family assignment(s)`) | PASSED | PASSED |
| (OBSOLETE: feature removed after this test) | Experience by use through real `player.attack` damage events (item mapping and tag mapping) | PASSED (family_a=3, family_b=1) | PASSED (family_a=5, family_b=1; extra events consistent with vanilla sweep hits, not proven) |
| Specialization: select, re-select (no-op), switch keeps previous branch progress (`value:42`), cancelled switch returns false | PASSED | PASSED |
| Technique unlock (true then false), challenge progress, invalid experience (0, -5) rejected | PASSED | PASSED |
| Death + respawn (clone) keeps all data | PASSED | PASSED |
| Nether travel keeps data | PASSED | PASSED |
| Client receives snapshots (8 packets; 6 mutations in one tick coalesce into 1) | PASSED | PASSED |
| Server restart: data reloaded from disk, datapack reloaded, further hit increments experience (3→4, 5→6) | PASSED | PASSED |
| Errors in logs | server 1 ERROR, client 1–4 ERROR (see below) | 0 / 0 |

Known log errors in A (not SAO+ code): client `Epic Fight web server … ConnectException` (EpicSkins, offline); one server `NullPointerException` in vanilla `DistanceManager` while handling the player's first `ServerboundMovePlayerPacket` at login, not reproduced in the restart pass or in B, cause not established.

Final artifact checks: `.\gradlew.bat clean build` BUILD SUCCESSFUL, 20/20 unit tests passed (0 failures), `build\libs\saoplus-1.0.0.jar` (42,496 bytes, 31 entries, no fixture classes) starts alone and with the 14 mods on a dedicated server with 0 errors and 0 crashes.

NOT VERIFIED in this round: any client screen or HUD (none was built); Epic Fight animations and attacks through Epic Fight (only vanilla `player.attack`); behavior of Epic Fight's own damage path with `WeaponUsageEvents`; projectile weapons; Pufferfish/RAS/Epic Fight API usage from Core code (no adapter exists); multiple simultaneous clients; Simply Tooltips visuals; sweep-hit cause.

## Level / experience / attributes - automated tests (2026-11-09)
`.\gradlew.bat clean build`: BUILD SUCCESSFUL. 63 tests, 63 passed, 0 failed, 0 skipped (ProgressionDataTest 28, ProgressionPersistenceTest 7, plus 28 earlier). Covered: curve, multi-level, surplus, max level, negative/extreme values, points and milestones, spending, softcap, respec, NBT round trip, clone, sync codec, old data, unknown fields, Weapon Mastery unchanged. NOT VERIFIED: anything in a real client or dedicated server (login, death, dimension, restart, multi-client).

## Epic Fight integration - automated and startup tests (2026-11-09)
`.\gradlew.bat clean build`: BUILD SUCCESSFUL, 70 tests, 0 failed, 0 skipped (new: WeaponFamilyCategoryTest 7). Isolated dedicated server (not production): Core alone starts (0 errors); Core + Epic Fight 20.14.17 starts and logs `Epic Fight detected` (0 errors). The first run without Epic Fight FAILED (ClassNotFoundException in `SaoPlus.<init>`); fixed by splitting guard and adapter (decision 26). NOT VERIFIED: category resolution with real items, Epic Fight damage reaching `CombatBridge`, animations, client.

## First playable version - tests (2026-11-09)
`.\gradlew.bat clean build`: see the final run below. Automated: 100 tests, 0 failed, 0 skipped (new: CombatFormulasTest 11, SwordSkillTest 10, ResourcePersistenceTest 6, ShippedDataTest 3). `ShippedDataTest` parses every shipped JSON with zero warnings. Isolated dedicated server (not production): Core alone and Core + Epic Fight 20.14.17 start with 0 error lines and log `Loaded 18 sword skill definition(s)`, `10 enemy profile`, `3 floor`, `56 weapon family assignment(s) from 9 file(s)`.
NOT VERIFIED (needs a person in a client): HUD/screen layout and key bindings; spending points and respec through the screen; a real kill giving XP/COL; HP modifier visible after levelling; MP regeneration; Sword Skill activation (key R), windup, execution, combo, cancel and interruption; mastery rank-up messages; damage numbers vs the formula with Epic Fight attacks; passive evasion; Epic Fight attack events reaching `CombatBridge`; death/respawn, dimension change and relog keeping all data; two simultaneous clients; the `/saoplus give` command from an FTB Quests reward; the `ItemAttributeModifierEvent` recursion risk with Epic Fight capabilities.

## Second iteration - tests (2026-11-09)
`.\gradlew.bat clean build`: see the final run. Automated: 111 tests, 0 failed, 0 skipped (new: GameplayDataTest 12 for specializations, zones, floors, shops, family rules, cleared floors, Epic Fight agility factors; ShippedDataTest now also validates specializations, shops, zones and that every branch skill has its branch).
Isolated dedicated server (fresh world, not production), three configurations, 0 error lines each: Core alone; Core + Epic Fight 20.14.17; Core + Epic Fight + Simply Swords + Basic Weapons + Architectury + FTB Library + FTB Teams + FTB Quests + Fzzy Config + Kotlin for Forge. Verified in the log: `FTB Teams detected`, `FTB Quests detected: reward type saoplus:reward registered`, `Epic Fight detected ... agility hooks and skill animations registered`, 36 skills, 13 enemy profiles, 3 floors loaded, dimensions `saoplus:aincrad_1..3` created and saved. Compile-time APIs of Epic Fight, FTB Quests/Teams/Library resolved from Modrinth / FTB maven.
NOT VERIFIED (needs a person in a client, ideally with two clients): everything in the previous list plus: the four-tab screen and shop screen layout; merchant/blacksmith/gate blocks (placing, right click, textures); buying, selling, repair; switching branch and its COL cost; branch effects in combat; Epic Fight animations triggered by skills (does `byKey` accept the shipped animation names?); stamina/attack-speed/counter hooks; Simply Swords and Basic Weapons movesets in Epic Fight (do the shipped capability files take effect?); safe-zone behaviour in a town box; floor access denial and gate travel; boss kill clearing a floor for a party; FTB Quests reward in the editor (config GUI) and claim; `saoplus-server.toml` correction message at first start (harmless).
