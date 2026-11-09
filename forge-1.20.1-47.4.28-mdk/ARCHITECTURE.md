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

## First playable version (added 2026-11-09)
| Package / class | Responsibility |
|---|---|
| `combat.GameplayConfig`, `CombatFormulas`, `WeaponProfile` | All provisional numbers, every formula (damage, crit, penetration, agility, resources, mastery ranks, rewards) and the weapon stat model. Pure, unit-tested. |
| `combat.CombatRules` | Listens to `PlayerAttackContextEvent`: applies the damage formula, crit, penetration, executes armed Sword Skills, awards mastery XP. Also passive evasion, knockback stability and weapon speed/reach item modifiers. |
| `resource.ResourceData`, `ResourceService` | Persistent MP, COL and highest floor; HP modifier from Vitality/level; MP regeneration. |
| `economy.EconomyService` | COL wallet API, respec payment gateway, repair cost, purchase helper. |
| `skill.SwordSkill`, `SkillStateMachine`, `SwordSkillService` | Datapack skill definitions, timing state machine (cooldown, windup, window, combo) and the server service (requirements, MP, interruption). |
| `service.MasteryProgress` | Mastery rank (derived), XP awarding, skill unlocks by rank. |
| `world.SaoPlusData`, `EnemyProfile`, `Floor`, `EnemyEvents` | Reload listeners for skills/enemy profiles/floors; enemy scaling; kill rewards; highest floor tracking. |
| `command.SaoPlusCommands` | Operator-only `/saoplus give` (bridge for FTB Quests command rewards). |
| `ClientRequestPacket` | Only client-to-server message: skill key, spend one point, respec. |
| `client.ClientSetup`, `ProgressionScreen` | Keys R/K, HUD, character screen (client only). |
Default content: `src/main/resources/data/saoplus/...` (9 families, 18 skills, 3 floors, 10 enemy profiles, 3 item tags) and `assets/saoplus/lang` (en_us, es_es). All of it is placeholder content in data, replaceable by datapacks.

Flow of a player hit: vanilla/Epic Fight damage event -> `CombatBridge` (is it a player hit?) -> `PlayerAttackContextEvent` -> `CombatRules` (formulas, skill, mastery) -> final damage. Kill: `EnemyEvents` -> XP (`ProgressionService`) + COL (`EconomyService`) -> sync.

## Second iteration: integration with the modpack (added 2026-11-09)
**Who does what.** SAO+ = RPG logic. Epic Fight = animations, dodge/guard/parry/stun/stamina. Simply Swords + Basic Weapons = weapons. FTB Quests = quests (SAO+ adds a reward type). FTB Teams = parties. Worldgen/structure/mob mods = the actual Aincrad content, plugged in through datapacks.

| New / changed piece | Responsibility |
|---|---|
| `compat.epicfight.EpicFightAdapter` | Also hooks Agility into Epic Fight (stamina cost, attack speed, dodge counter window) and plays skill animations. |
| `compat.ftb.FtbCompat` -> `FtbTeamsAdapter`, `FtbQuestsAdapter` | Guarded, optional. Party provider; `saoplus:reward` quest reward type. |
| `service.PartyService`, `service.WeaponResolver` | Party members in range; active family (composites, unlock rules). |
| `skill.BranchRules`, `Specialization`, `CounterWindow`, `SkillPresentation` | Specialization progression and effects; counter window; skill animation/sound/particles. |
| `family.FamilyRule` | `unlock` / `composite` rules inside the family data. |
| `economy.Shop`, `ShopService`, `MerchantBlock`, `ModContent` | Data-driven shops and blacksmiths, merchant/blacksmith/gate blocks. |
| `world.Zone`, `Floor`, `WorldRules`, `GateBlock`, `EnemyEvents` | Towns (safe), zones, floor access control, bosses, enemy scaling and rewards. |
| `CatalogPacket`, `OpenShopPacket`, `ClientRequestPacket` | Server -> client catalog and "open shop"; client -> server requests (skill, spend, respec, select branch, buy, sell, repair). |
| `client.ProgressionScreen`, `ShopScreen`, `ClientSetup`, `SaoStyle`, `ClientCatalog` | SAO-styled HUD and screens. |

Data folders (all under `data/<namespace>/saoplus/`): `weapon_families`, `sword_skills`, `specializations`, `enemy_profiles`, `floors`, `zones`, `shops`. Also `data/<mod>/capabilities/weapons` (Epic Fight movesets) and `data/saoplus/dimension(_type)` (floor skeletons).
