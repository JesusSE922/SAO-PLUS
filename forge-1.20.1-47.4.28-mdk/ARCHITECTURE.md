# SAO+ Core — Architecture

Minecraft 1.20.1, Forge 47.4.28, Java 17. Mod id `saoplus`. The Core has **no compile-time or runtime dependency** on any other mod; integrations are meant to be optional adapters.

## Principles
- SAO+ Core owns SAO+-specific rules and data (decision recorded in `TECHNICAL_DECISIONS.md`); other mods supply events and information through adapters.
- The GDD leaves most numbers and catalogs undefined. The Core therefore ships **mechanisms, not content**: no weapon family, branch, rank, formula or reward is hard-coded. Content comes from datapacks and configuration.
- The server is authoritative; clients receive read-only snapshots.

## Packages (`com.saoplus.core`)
| Package / class | Responsibility |
|---|---|
| `SaoPlus`, `SaoPlusConfig` | Mod entry; per-world server config (placeholder `masteryExperiencePerHit`, not a GDD value). |
| `PlayerData`, `PlayerDataCapability`, `PlayerDataProvider`, `PlayerDataEvents` | Capability attach, NBT save/load, clone on death/dimension, login/respawn/dimension sync. Typed components live under their own NBT keys beside the untyped `CompoundTag`. |
| `WeaponMasteryData`, `WeaponMasteryRecord` | Storage-only mastery: independent record per family (experience, challenges, unlocked techniques), open `ResourceLocation` keys. |
| `SpecializationData` | One active branch per family, per-branch progress preserved across switches (GDD 6.3). |
| `ModNetwork`, `PlayerDataSyncPacket` | `saoplus:main` channel, one server → client snapshot message; snapshots above 1.9 MB are refused and logged; a missing capability is logged, not thrown. |
| `PlayerDataSync` | Marks a player dirty; one snapshot per player is sent at the end of the tick, however many changes occurred. |
| `family.WeaponFamilyIndex`, `family.WeaponFamilyManager` | Datapack catalog `data/<ns>/saoplus/weapon_families/*.json` (`{"family","items","tags"}`) → family of a held item. Conflicts: first file by id wins, with a warning. Ships empty. |
| `service.WeaponMasteryService` | Only way to change mastery: `addExperience`, `setChallengeProgress`, `unlockTechnique`. Values only, no rules. Posts `WeaponMasteryChangedEvent`. |
| `service.SpecializationService` | `select(player, family, branch)`; posts cancelable `SpecializationChangedEvent` first so future limits can veto. |
| `service.WeaponUsageEvents` | Experience by use: direct melee `LivingDamageEvent` by a server player holding a configured-family weapon adds `masteryExperiencePerHit`. Uses only vanilla Forge events, so it works with any combat mod that deals damage through them. |

## Data flow
1. Source (combat event, future quest/skill rule) → service (server) → data in the `PlayerData` capability → `PlayerDataSync.markDirty`.
2. End of tick → `ModNetwork.syncPlayerData` → `PlayerDataSyncPacket` → client mirror.
3. Save: provider `serializeNBT`; clone: `PlayerData.copyFrom` (deep); sync on login, respawn and dimension change.

## Integration points (candidate mods; none is a dependency)
Evidence and status are in `DEPENDENCY_COMPATIBILITY.md`. Planned adapters, each behind a "mod loaded" check and in its own package, not yet written:
- Epic Fight: `PlayerEventListener` events (`DEAL_DAMAGE_*`, `DODGE_SUCCESS_EVENT`, `SKILL_CAST_EVENT`) and weapon presets/data mapping.
- RPG Attribute System: `RasApi.getLevel/getCombatSnapshot` for display and requirements.
- Pufferfish's Skills: `SkillsAPI.registerReward/registerExperienceSource` to express specialization effects.
They are not implemented because the Core currently needs none of their APIs and adding them would add compile dependencies without a consumer (see `TECHNICAL_DECISIONS.md`).

## Not implemented (not defined by the GDD, or outside the Core's current scope)
Ranks, thresholds, formulas, rewards, challenge definitions; specialization branches and their effects; switching limits/consequences; Sword Skills, MP and cooldowns; blocking/dodging/parrying rules; economy (COL, shops); quests; Aincrad floor progression and world access; client screens and HUD; projectile-weapon experience.
