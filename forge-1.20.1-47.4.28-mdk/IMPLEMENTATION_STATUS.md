# SAO+ Core — Implementation Status

Updated 2026-11-09. Evidence: `TESTING_REPORT.md`; architecture: `ARCHITECTURE.md`; decisions: `TECHNICAL_DECISIONS.md`.

## Implemented (GDD-confirmed behavior, mechanism only)
- Player data foundation: capability, attach, NBT save/load, clone on death/dimension, sync on login/respawn/dimension change.
- **Weapon Mastery data (GDD 7.2)**: independent record per weapon family — experience, challenges, unlocked techniques — kept when switching weapons. Server-side `WeaponMasteryService` with change events; coalesced client sync.
- **Experience by use (GDD 7.2)**: direct melee damage by a player holding a weapon of a configured family adds `masteryExperiencePerHit` (placeholder, default 1) to that family.
- **Weapon family catalog**: datapack `data/<ns>/saoplus/weapon_families/*.json` (items and/or item tags → family). Ships empty on purpose: the GDD has not fixed the families.
- **Specialization (GDD 6.3)**: one active branch per family; switching keeps the previous branch's progress and restores it on re-selection; inactive branches report inactive. `SpecializationService` with a cancelable event for future limits.
- Network hardening: missing capability logged instead of thrown; snapshots over 1.9 MB refused and logged; per-tick coalescing of syncs.

## Not implemented
Not defined by the GDD (cannot be built without inventing design): mastery ranks/thresholds/formulas/rewards/challenge definitions; specialization branches and effects; switching limits/consequences; Sword Skills, MP, cooldowns; block/dodge/parry rules and their per-specialization effectiveness; economy (COL, shops, blacksmiths); quest content; Aincrad floor progression and world access; boss mechanics.
Defined but not built in this round: client screens and HUD (holographic menu, mastery/skill screens), projectile-weapon experience, adapters for Epic Fight / RPG Attribute System / Pufferfish's Skills (evidence for them is in `DEPENDENCY_COMPATIBILITY.md`; none is a dependency yet).

## Known limitations
- Sync is a full snapshot (one per player per tick when dirty).
- `getOrCreate` on `WeaponMasteryData` still exposes a live record for in-process callers; external code should use the service.
- With vanilla sweeping, one swing can damage several entities and each counts as a hit (observed difference: 5 vs 3 experience for the same 3 swings with and without Epic Fight installed; cause consistent with sweep damage, not proven).
- Open reviews from earlier phases: `LazyOptional` renewal scope in `PlayerDataProvider` (kept as validated in game).

## Decisions requiring user approval
- Merge of `fix/player-capability-clone` into `master`; commit of the pending work (nothing was committed or pushed in this round).
- Which candidate mods become dependencies, and the weapon family / branch catalog.
