# SAO+ Core — Implementation Status

Updated 2026-11-09. See `TESTING_REPORT.md` for evidence and `REPOSITORY_AUDIT.md` for risks.

## Completed
- Player data foundation: capability, attach, NBT save/load, clone, sync on login/respawn/dimension change (VERIFIED IN GAME).
- Weapon Mastery data contract and persistence (storage only): per-family records keyed by an open `ResourceLocation`; experience, challenges (untyped), unlocked techniques. No ranks, formulas, rewards or production family ids. Persisted and synced inside `PlayerData` (VERIFIED IN GAME, 9 unit tests).
- Phase 0 audit, Phase 1 dedicated-server validation.

## Pending (Phase 1 is closed documentally, not fully)
Open technical reviews (no defect demonstrated yet):
- `LazyOptional` renewal scope in `PlayerDataProvider`.
- Missing-capability handling in `ModNetwork.syncPlayerData` (throws today; clone handler only logs).
- Packet hardening: malformed NBT handling, protocol-version policy.

Pending validations:
- 2 MiB packet size limit with large data.
- Multiple simultaneous clients.
- Full `.\gradlew.bat build`.

Also pending: post-change sync hook for future services (design only).

## Not started
Phases 2–10 (dependency validation, architecture, RPG progression, skills, combat, UI, quests/economy, world progression, release).

## Decisions requiring user approval
- Merge `fix/player-capability-clone` into `master` (not done).
- Commit of the new documentation files and of the staged GDD (not done).
- Start of Phase 2.

## Known limitations
- Sync is full-snapshot and event-driven only.
- Mastery has no mutation service yet; `getOrCreate` exposes live records.
