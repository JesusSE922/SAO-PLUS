# SAO+ Core — Implementation Status

Updated 2026-11-09. Evidence: `TESTING_REPORT.md`; architecture: `ARCHITECTURE.md`; decisions: `TECHNICAL_DECISIONS.md`.

## Implemented (GDD-confirmed behavior, mechanism only)
- Player data foundation: capability, attach, NBT save/load, clone on death/dimension, sync on login/respawn/dimension change.
- **Weapon Mastery data (GDD 7.2)**: independent record per weapon family — experience, challenges, unlocked techniques — kept when switching weapons. Server-side `WeaponMasteryService` with change events; coalesced client sync.
- **No automatic mastery progress.** Experience by hit was removed (obsolete, decision 12). `WeaponMasteryService` remains as the only write API but has no automatic callers.
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
