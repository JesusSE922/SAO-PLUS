# SAO+ Core — Technical Decisions

| # | Decision | Source | Date |
|---|---|---|---|
| 1 | Target Minecraft 1.20.1, Forge 47.4.28, Java 17; Forge-only APIs. | CLAUDE.md / roadmap | — |
| 2 | Weapon Mastery is owned by SAO+ Core; external mods may only provide events/information through adapters. | User (CLAUDE.md-level decision; GDD §16 has not assigned systems yet) | 2026-11-09 |
| 3 | Weapon Mastery currently ships only its data contract and persistence: independent records per family, extensible, NBT, synced, copied on clone. No ranks, formulas, rewards or invented ids. | User | 2026-11-09 |
| 4 | Mastery lives in `PlayerData` under NBT key `weapon_mastery`, omitted when empty so Phase 1 NBT stays identical. | Proposal, implemented | 2026-11-09 |
| 5 | Family ids are open `ResourceLocation`s; production ids are undefined. Tests use only `saoplus_test:*`. | Proposal, implemented | 2026-11-09 |
| 6 | `.mcp.json` stays tracked and untouched. | User | 2026-11-09 |
| 7 | Real-game tests use a temporary dev-only fixture that is removed before finishing; no debug commands. | User | 2026-11-09 |
| 8 | A local dedicated server and clients may be used for validation in an isolated environment. EULA accepted by the user. | User | 2026-11-09 |
| 9 | JUnit 5.10.2 added as a test-scope dependency only. | User approved (step 2) | 2026-11-09 |
| 10 | No merge to `master` yet. | Assistant default, pending user | 2026-11-09 |
| 11 | Weapon families come from datapacks (`saoplus/weapon_families`) and ship empty; no family, branch or weapon id is hard-coded. Reason: the GDD catalog of nine families is only "proposed". | Assistant, per GDD 7.1 | 2026-11-09 |
| 12 | Experience by use listens to vanilla `LivingDamageEvent` (direct melee only), so it needs no dependency on Epic Fight or other combat mods. Amount per hit is a config placeholder (`masteryExperiencePerHit`, default 1), not a GDD value. | Assistant | 2026-11-09 |
| 13 | `SpecializationData` stores branch progress as opaque NBT and the switch posts a cancelable event, because the GDD says switching is limited with consequences but defines neither. | Assistant, per GDD 6.3 | 2026-11-09 |
| 14 | Data changes are coalesced: services mark the player dirty and one snapshot is sent at the end of the tick. | Assistant | 2026-11-09 |
| 15 | Syncing a player without the capability logs a warning instead of throwing; snapshots above 1.9 MB are refused (client limit is 2 MiB). | Assistant (Phase 1 review item) | 2026-11-09 |
| 16 | No compile dependency was added on Epic Fight, RAS or Pufferfish's Skills: no Core feature consumes their APIs yet and the GDD has not assigned systems to origins (GDD 16). Adapters stay planned, in separate packages. | Assistant | 2026-11-09 |
| 17 | `LazyOptional` renewal in `PlayerDataProvider` left unchanged (validated in game in Phase 1). | Assistant | 2026-11-09 |
