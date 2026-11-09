# SAO+ Core — Implementation Status

Updated 2026-11-09. Evidence: `TESTING_REPORT.md`; architecture: `ARCHITECTURE.md`; decisions: `TECHNICAL_DECISIONS.md`.

## Implemented (GDD-confirmed behavior, mechanism only)
- Player data foundation: capability, attach, NBT save/load, clone on death/dimension, sync on login/respawn/dimension change.
- **Weapon Mastery data (GDD 7.2)**: independent record per weapon family — experience, challenges, unlocked techniques — kept when switching weapons. Server-side `WeaponMasteryService` with change events; coalesced client sync.
- **Mastery progress by hit: SUPERSEDED.** It was removed on 2026-11-09 and re-introduced with ranks in the first playable version (decision 33); see the system table below.
- **Weapon family catalog**: datapack `data/<ns>/saoplus/weapon_families/*.json` (items and/or item tags → family). Ships empty on purpose: the GDD has not fixed the families.
- **Persistence details**: root key `saoplus:weapon_mastery`; legacy `weapon_mastery` migrated only if it looks like mastery data (idempotent; if both keys exist the namespaced one wins and the legacy data is kept untouched); unknown fields inside each family record are preserved on load, save and clone.
- **Validation**: 28 automated tests (0 failures, 0 skipped) and `clean build` pass. **In-game validation (client, dedicated server) of this latest change is pending.**
- **Specialization (GDD 6.3)**: one active branch per family; switching keeps the previous branch's progress and restores it on re-selection; inactive branches report inactive. `SpecializationService` with a cancelable event for future limits.
- Network hardening: missing capability logged instead of thrown; snapshots over 1.9 MB refused and logged; per-tick coalescing of syncs.

## Not implemented
Not defined by the GDD (cannot be built without inventing design): mastery ranks/thresholds/formulas/rewards/challenge definitions; specialization branches and effects; switching limits/consequences; Sword Skills, MP, cooldowns; block/dodge/parry rules and their per-specialization effectiveness; economy (COL, shops, blacksmiths); quest content; Aincrad floor progression and world access; boss mechanics.
Defined but not built in this round: client screens and HUD (holographic menu, mastery/skill screens), projectile-weapon experience, adapters for Epic Fight / RPG Attribute System / Pufferfish's Skills (evidence for them is in `DEPENDENCY_COMPATIBILITY.md`; none is a dependency yet).

## Known limitations
- Sync is a full snapshot (one per player per tick when dirty).
- `getOrCreate` on `WeaponMasteryData` still exposes a live record for in-process callers; external code should use the service.
- Open reviews from earlier phases: `LazyOptional` renewal scope in `PlayerDataProvider` (kept as validated in game).

## Decisions requiring user approval
- Merge of `fix/player-capability-clone` into `master`; commit of the pending work (nothing was committed or pushed in this round).
- Which candidate mods become dependencies, and the weapon family / branch catalog.

## Level, experience and attributes (2026-11-09)
Implemented (data, rules, API, persistence; automated tests only): level 1-100 with configurable curve, automatic multi-level up with carried surplus, max-level behavior, attribute points per level and milestones (never rewarded twice), six attributes with manual spending, softcap cost, respec service refunding the exact invested points, clone/save/sync through `PlayerData`, `PlayerLevelChangedEvent`. See `SAO_plus_informe_implementacion_level_experience.md`.
Provisional values: `TECHNICAL_DECISIONS.md` 20-23 and `ProgressionConfig`. COL: no economy exists; respec returns `NO_ECONOMY` until a `CurrencyGateway` is installed. Not implemented: experience sources, attribute effects (health, MP, damage, loot), UI, item requirements. In-game validation (client, dedicated server): **pending**. Tests: 63, 0 failures.

## Epic Fight integration (2026-11-09)
Implemented: optional adapter (compile-only dependency, optional in `mods.toml`), weapon categories as a family source (`"categories"` in family data), opaque per-item `"stats"`, `CombatBridge` + `PlayerAttackContextEvent` connecting level, attributes and family to melee hits (multiplier 1.0 until a combat design exists), including Epic Fight damage sources. Weapon Mastery relation: a stack resolves to a family through item, tag or Epic Fight category; no mastery progress is granted (decision 12 stays obsolete). External weapon movesets are mapped with Epic Fight datapacks or CompatLink. **Not verified in game**: category resolution on a real item, Epic Fight attacks reaching `CombatBridge`, animations. Server startup verified with and without Epic Fight. See `SAO_plus_informe_integracion_epicfight.md`.

## First playable version (2026-11-09) - state per system
Legend: FUNCTIONAL = implemented and covered by automated tests/startup, **not yet played in game**; PARTIAL = part of the brief missing; CONTENT = mechanism done, needs content; BLOCKED = external limitation.
| System | State |
|---|---|
| Progression (level, XP, points, attributes, softcap, respec) | FUNCTIONAL. Attribute effects: Strength, Vitality, Intelligence, Luck, Dexterity, Agility all feed a formula (decisions 31, 35, 36). |
| HP / MP / COL | FUNCTIONAL (HP = vanilla health + modifier; MP regen; COL wallet). |
| Combat (damage formula, crit, penetration, weapon stats) | FUNCTIONAL for hits that go through Forge damage events (vanilla and Epic Fight). Epic Fight dodge/guard/parry/stun are not modified: PARTIAL. |
| Weapon families and external weapons by data | FUNCTIONAL; 9 families mapped to Epic Fight categories, Basic Weapons items; Simply Swords items are not mapped (CONTENT). |
| Weapon Mastery (XP, ranks, unlocks) | FUNCTIONAL. Specialization branches have storage only: no branch effects, no switching limits (GDD undefined): PARTIAL. |
| Sword Skills | FUNCTIONAL as damage-multiplier skills (MP, cooldown, windup, window, combos, cancel, interruption). Visual animations/effects and Epic Fight animation playback are not triggered: PARTIAL. 18 placeholder skills (CONTENT). |
| Enemies, scaling, rewards | FUNCTIONAL for hostile mobs; floor multipliers; no bosses/AI behaviours (CONTENT). |
| Economy | PARTIAL: wallet, rewards, respec, repair API, purchase helper. No shops/blacksmith/repair blocks (CONTENT). |
| Quests | PARTIAL: via FTB Quests command rewards (decision 40); no FTB Teams party XP sharing, no custom quest tasks. |
| Interface | FUNCTIONAL (compiles, loads on server): HUD, character screen, keys. Client never run in this round: layout unverified. No skill cooldown display, no skill list UI. |
| World / floors | PARTIAL: floor data, scaling, highest floor stored. No Aincrad dimensions, no access control between floors. |
| Materials, plants, dungeons, bosses | CONTENT: not implemented. |
Verification: 100 automated tests pass; isolated dedicated server starts with and without Epic Fight and loads all data (18 skills, 10 enemy profiles, 3 floors, 56 family assignments). **Nothing was played in a client**: see TESTING_REPORT.md for the manual checklist.

## Second iteration (2026-11-09) - state per system (supersedes the table above where they differ)
Legend: FUNCTIONAL = implemented, automated tests/startup pass, **not played in a client**; PARTIAL; CONTENT = mechanism done, needs content; BLOCKED = external limitation.
| System | State |
|---|---|
| Stability (Epic Fight capability recursion) | Preventive fix (decision 43): guard + cache. Not reproducible, not proven in game. |
| Combat with Epic Fight (Agility) | FUNCTIONAL via stamina, attack speed and dodge-counter hooks. PARTIAL: stun duration, parry windows and guard strength are not touched (no safe API). |
| Weapons | FUNCTIONAL: nine families, Simply Swords (typed weapons) and Basic Weapons mapped with Epic Fight movesets; sword-and-shield and dual wield resolved as composites; dual wield unlock rule. CONTENT: Simply Swords unique weapons, other mods' weapons, per-weapon stats (only family defaults exist). |
| Sword Skills | FUNCTIONAL with Epic Fight animations, sound, particles, heal, knockback; 36 placeholder skills. PARTIAL: no cancel-by-dodge, no client cooldown display, animations not verified. |
| Specializations | FUNCTIONAL (decision 46): requirements, switch cost, effects with drawbacks, branch progression, exclusive skills, UI. CONTENT: 18 placeholder branches. |
| Economy | FUNCTIONAL: COL, shops, blacksmith repair, merchant blocks, respec payment. CONTENT: catalogs/prices are placeholders; no reputation or dynamic stock. |
| Multiplayer | FUNCTIONAL (FTB Teams): shared kill rewards, shared boss clears. Not tested with two clients. Authority stays on the server. |
| Aincrad | PARTIAL: three skeleton floors (dimensions load and save on a dedicated server), access control, gates, towns as safe zones, labyrinth zones, floor bosses (placeholder vanilla mobs), scaling. CONTENT: real terrain, towns, labyrinths, dungeons, materials, boss mobs and loot. |
| Quests | FUNCTIONAL: native FTB Quests reward type. |
| Interface | FUNCTIONAL (compiles, loads on server): SAO-styled HUD, 4-tab character screen, shop screen. Layout never seen in a client. |
| Bosses / dungeons / materials / plants | CONTENT: not created; the data hooks exist (enemy profile `boss_floor`, zones, shops, loot via the pack). |
Verification: 111 automated tests pass. Isolated dedicated server (not production), fresh world: Core alone, Core + Epic Fight, and Core + Epic Fight + Simply Swords + Basic Weapons + FTB Library/Teams/Quests + Architectury start with 0 error lines; adapters register; the three Aincrad dimensions are created and saved.
