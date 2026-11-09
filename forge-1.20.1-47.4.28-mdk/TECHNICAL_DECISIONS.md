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
