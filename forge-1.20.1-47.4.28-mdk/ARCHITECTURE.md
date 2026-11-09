# SAO+ Core — Architecture

Minecraft 1.20.1, Forge 47.4.28, Java 17. Mod id `saoplus`. The Core has **no compile-time or runtime dependency** on any other mod; integrations are meant to be optional adapters.

## Principles
- SAO+ Core owns SAO+-specific rules and data (decision recorded in `TECHNICAL_DECISIONS.md`); other mods supply events and information through adapters.
- The GDD leaves most numbers and catalogs undefined. The Core therefore ships **mechanisms, not content**: no weapon family, branch, rank, formula or reward is hard-coded. Content comes from datapacks and configuration.
- The server is authoritative; clients receive read-only snapshots.

## Packages (`com.saoplus.core`)
| Package / class | Responsibility |
|---|---|
| `SaoPlus` | Mod entry. There is no config: the former placeholder `masteryExperiencePerHit` was removed (see TECHNICAL_DECISIONS 12). |
| `PlayerData`, `PlayerDataCapability`, `PlayerDataProvider`, `PlayerDataEvents` | Capability attach, NBT save/load, clone on death/dimension, login/respawn/dimension sync. Typed components live under their own NBT keys beside the untyped `CompoundTag`. |
| `WeaponMasteryData`, `WeaponMasteryRecord` | Storage-only mastery: independent record per family (experience, challenges, unlocked techniques), open `ResourceLocation` keys. |
| `SpecializationData` | One active branch per family, per-branch progress preserved across switches (GDD 6.3). |
| `ModNetwork`, `PlayerDataSyncPacket` | `saoplus:main` channel, one server → client snapshot message; snapshots above 1.9 MB are refused and logged; a missing capability is logged, not thrown. |
| `PlayerDataSync` | Marks a player dirty; one snapshot per player is sent at the end of the tick, however many changes occurred. |
| `family.WeaponFamilyIndex`, `family.WeaponFamilyManager` | Datapack catalog `data/<ns>/saoplus/weapon_families/*.json` (`{"family","items","tags"}`) → family of a held item. Conflicts: first file by id wins, with a warning. Ships empty. |
| `service.WeaponMasteryService` | Only way to change mastery: `addExperience`, `setChallengeProgress`, `unlockTechnique`. Values only, no rules. Posts `WeaponMasteryChangedEvent`. |
| `service.SpecializationService` | `select(player, family, branch)`; posts cancelable `SpecializationChangedEvent` first so future limits can veto. |

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

## Level, experience and attributes (added 2026-11-09)
| Class | Responsibility |
|---|---|
| `progression.ProgressionConfig` | All provisional numbers (curve, points, milestones, softcap, limits, respec cost). |
| `progression.ExperienceCurve`, `LevelRewards`, `Softcap` | Pure formulas: experience to next level, points for reaching a level, attribute cost. |
| `progression.AttributeType` | STRENGTH, VITALITY, DEXTERITY, AGILITY, INTELLIGENCE, LUCK. |
| `progression.ProgressionData` | Stored in `PlayerData` (`saoplus:progression`): level, experience, total, available points, rewarded level, per-attribute value/invested; unknown fields preserved. |
| `progression.CurrencyGateway` | Future COL integration point; none installed. |
| `service.ProgressionService` | Public API: reads and server-only writes (`addExperience`, `grantAttributePoints`, `spendPoints`, `respecAttributes`). Posts `PlayerLevelChangedEvent`. |

Sync reuses the full `PlayerData` snapshot; no new packet. Not implemented: experience sources, attribute effects, UI, commands.

## Epic Fight integration (added 2026-11-09)
| Class | Responsibility |
|---|---|
| `compat.epicfight.EpicFightCompat` | Guard called from `SaoPlus`; references no Epic Fight type, so the Core starts without Epic Fight. |
| `compat.epicfight.EpicFightAdapter` | Linked only when Epic Fight is loaded: registers a `WeaponCategorySource` (item capability to `epicfight:<category>`) and declares `EpicFightDamageSource` as player melee. |
| `family.WeaponCategorySource` | Extension point for external weapon categories; family data maps them with `"categories"`. |
| `service.CombatBridge`, `PlayerAttackContextEvent` | Per melee hit: posts level, attributes and family; applies the multiplier listeners set (default 1.0, no Core formula). |

Family resolution order: item id, item tag, external category. Weapon stats per item: `WeaponFamilyManager.statsOf` (opaque).
