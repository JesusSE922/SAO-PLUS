# SAO+ Core — Technical Decisions

| # | Decision | Source | Date |
|---|---|---|---|
| 1 | Target Minecraft 1.20.1, Forge 47.4.28, Java 17; Forge-only APIs. | CLAUDE.md / roadmap | — |
| 2 | Weapon Mastery is owned by SAO+ Core; external mods may only provide events/information through adapters. | User (CLAUDE.md-level decision; GDD §16 has not assigned systems yet) | 2026-11-09 |
| 3 | Weapon Mastery currently ships only its data contract and persistence: independent records per family, extensible, NBT, synced, copied on clone. No ranks, formulas, rewards or invented ids. | User | 2026-11-09 |
| 4 | Mastery lives in `PlayerData` under NBT key `saoplus:weapon_mastery` (was `weapon_mastery`; the old key is migrated when its content looks like mastery data), omitted when empty. | Updated | 2026-11-09 |
| 5 | Family ids are open `ResourceLocation`s; production ids are undefined. Tests use only `saoplus_test:*`. | Proposal, implemented | 2026-11-09 |
| 6 | `.mcp.json` stays tracked and untouched. | User | 2026-11-09 |
| 7 | Real-game tests use a temporary dev-only fixture that is removed before finishing; no debug commands. | User | 2026-11-09 |
| 8 | A local dedicated server and clients may be used for validation in an isolated environment. EULA accepted by the user. | User | 2026-11-09 |
| 9 | JUnit 5.10.2 added as a test-scope dependency only. | User approved (step 2) | 2026-11-09 |
| 10 | No merge to `master` yet. | Assistant default, pending user | 2026-11-09 |
| 11 | Weapon families come from datapacks (`saoplus/weapon_families`) and ship empty; no family, branch or weapon id is hard-coded. Reason: the GDD catalog of nine families is only "proposed". | Assistant, per GDD 7.1 | 2026-11-09 |
| 12 | **OBSOLETE (discarded 2026-11-09).** Experience by hit via `LivingDamageEvent` and the `masteryExperiencePerHit` config were removed; mastery progression and technique unlocks will be defined in a later phase. Current: data model, `unlocked_techniques` and `WeaponMasteryService` kept without automatic callers. | User / assistant | 2026-11-09 |
| 13 | `SpecializationData` stores branch progress as opaque NBT and the switch posts a cancelable event, because the GDD says switching is limited with consequences but defines neither. | Assistant, per GDD 6.3 | 2026-11-09 |
| 14 | Data changes are coalesced: services mark the player dirty and one snapshot is sent at the end of the tick. | Assistant | 2026-11-09 |
| 15 | Syncing a player without the capability logs a warning instead of throwing; snapshots above 1.9 MB are refused (client limit is 2 MiB). | Assistant (Phase 1 review item) | 2026-11-09 |
| 16 | No compile dependency was added on Epic Fight, RAS or Pufferfish's Skills: no Core feature consumes their APIs yet and the GDD has not assigned systems to origins (GDD 16). Adapters stay planned, in separate packages. | Assistant | 2026-11-09 |
| 17 | `LazyOptional` renewal in `PlayerDataProvider` left unchanged (validated in game in Phase 1). | Assistant | 2026-11-09 |
| 18 | General progression (level, experience, attribute points, six attributes) lives in `PlayerData` under NBT key `saoplus:progression` (class `ProgressionData`), omitted while default; no second data source. | Assistant, per user brief | 2026-11-09 |
| 19 | **Approved by the brief:** initial level 1, initial max level 100, six attributes (Strength, Vitality, Dexterity, Agility, Intelligence, Luck), points on level-up plus milestones, softcap relative to level, respec paid in COL. | User | 2026-11-09 |
| 20 | **PROVISIONAL** curve `xp(L -> L+1) = round(100 * L^1.6)`; 2 points per level gained, +5 at every level multiple of 10; attribute base value 1, max 999. All in `ProgressionConfig`. | Assistant | 2026-11-09 |
| 21 | **PROVISIONAL** softcap: values below `level + 5` cost 1 point; from the threshold cost = `2 + (value - threshold) / 5`. Never blocks; attributes may exceed the level. | Assistant | 2026-11-09 |
| 22 | Each attribute stores `invested` (points actually paid); respec resets all attributes to base and refunds exactly that, so points cannot be duplicated. `rewarded_level` ensures a level is never rewarded twice. | Assistant | 2026-11-09 |
| 23 | No COL system exists. `CurrencyGateway` is the integration point; with none installed `respecAttributes` returns `NO_ECONOMY` and changes nothing (no simulated payment). Provisional cost 100 COL per level. | Assistant | 2026-11-09 |
| 24 | Server authority: all writes go through `ProgressionService` with `ServerPlayer`; no client-to-server packet, no command or debug tool. Experience sources (kills, quests...) are not implemented. | User brief | 2026-11-09 |
| 25 | Epic Fight 20.14.17 (Modrinth version `KEBfkBat`) is a **compile-only** dependency (`fg.deobf`, Modrinth maven) and an optional mod dependency in `mods.toml` (`mandatory=false`, `[20.14,)`, `AFTER`). It is needed to read item weapon categories and recognize its damage sources. Supersedes decision 16 for Epic Fight only; RAS and Pufferfish stay undeclared. | Assistant, per user brief | 2026-11-09 |
| 26 | All Epic Fight code lives in `compat.epicfight`; `EpicFightCompat` (no EF types) guards `EpicFightAdapter`. A first version referenced EF types from the guard class and crashed the Core when EF was absent (found by a server test, fixed). | Assistant | 2026-11-09 |
| 27 | The Epic Fight weapon category (`epicfight:<name>`, e.g. greatsword) is exposed as an external category; weapon family data maps categories to families with `"categories"`. Items and tags win over categories. Which family owns which category is data, not code: none is shipped. | Assistant | 2026-11-09 |
| 28 | `CombatBridge` posts `PlayerAttackContextEvent` (level, attributes, family) for player melee hits and applies a damage multiplier that starts at 1.0. **PROVISIONAL / PENDING DESIGN:** no combat formula exists, so the Core never changes it. Mastery progress by hit stays removed (see 12). | Assistant | 2026-11-09 |
| 29 | Optional per-item `"stats"` in family data are stored as opaque numbers (`WeaponFamilyManager.statsOf`) and have no effect yet; their meaning is pending design. Epic Fight movesets for external weapons are mapped with Epic Fight datapacks (`capabilities/weapons`) or CompatLink, not by SAO+. | Assistant | 2026-11-09 |
