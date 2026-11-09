# SAO+ Core — Dependency Compatibility (Phase 2, stages 1–5)

Date: 2026-11-09 · Target: Minecraft 1.20.1, Forge 47.4.28, Java 17.
These are **candidates, not approved dependencies**. Nothing was added to the Core build and no production code was changed.

Labels: **VERIFIED IN GAME** (dedicated server started with the jars), **VERIFIED BY CODE** (metadata read from the jar / public repository), **NOT VERIFIED**.

## 1. Exact artifacts (all downloaded and hash-verified)

| Candidate | Exact file | Source | Channel | Hash |
|---|---|---|---|---|
| Epic Fight 20.14.17 | `epic-fight-20.14.17-mc1.20.1-forge.jar` | Modrinth | release | sha512 OK |
| Pufferfish's Skills 0.19.2 | `puffish_skills-0.19.2-1.20-forge.jar` | Modrinth | **beta** | sha512 OK |
| RPG Attribute System 4.2.6 | `rpg_attribute_system-forge-1.20.1-4.2.6.jar` | Modrinth (project now titled "Remnants \| RPG Stats, Skills & Progression", id `d85UTOuq`) | release | sha512 OK |
| JAUML 2.3.1 | `jauml-forge-1.20.1-2.3.1.jar` | Modrinth | release | sha512 OK |
| Simply Swords 1.70.2 | `simplyswords-forge-neoforge-1.70.2-1.20.1.jar` | Modrinth | release | sha512 OK |
| Basic Weapons 1.2.4+1.20.1 | `basicweapons-1.2.4+1.20.1.jar` | Modrinth | release | sha512 OK |
| CompatLink 1.1.2 | `compatlink-1.1.2.jar` (Modrinth project "CompatLink - Epic Fight Weapons Compat") | Modrinth | release | sha512 OK |
| FTB Quests 2001.4.22 | `ftb-quests-forge-2001.4.22.jar` | maven.ftb.dev | — | sha1 OK |
| FTB Teams 2001.3.2 | `ftb-teams-forge-2001.3.2.jar` | maven.ftb.dev | — | sha1 OK |
| FTB Library | `ftb-library-forge-2001.2.13.jar` (also 2001.2.9, the version the Quests POM lists; not run) | maven.ftb.dev | — | sha1 OK |
| Architectury API | `architectury-9.2.14-forge.jar` | Modrinth | release | sha512 OK |

**Additional required libraries not on the original list** (found from `mods.toml`):
- Fzzy Config `0.7.7+1.20.1+forge` — required by Simply Swords (≥0.6.6) and Simply Tooltips.
- Kotlin for Forge `4.12.0` — required by Fzzy Config (`[4.11.0,4.99.0]`).
- Simply Tooltips `0.1.5-1.20.1` — required by Simply Swords, declared side CLIENT.

First-attempt downloads of Pufferfish and CompatLink selected the wrong loader (Fabric 26.1 / NeoForge); they were discarded and re-downloaded with a Forge 1.20.1 filter.

## 2. Declared dependencies (from each jar's `mods.toml`) — VERIFIED BY CODE

| Mod | Mandatory | Optional |
|---|---|---|
| Epic Fight 20.14.17 | Forge ≥47.4.0, MC [1.20,1.21) | — |
| Pufferfish's Skills 0.19.2 | Forge ≥46, MC [1.20,1.21) | — |
| RPG Attribute System 4.2.6 | Forge ≥47.2.30, MC [1.20.1,1.22), **JAUML (any)** | — |
| JAUML 2.3.1 | Forge ≥47, MC [1.20.1,1.21) | — |
| FTB Quests 2001.4.22 | Architectury ≥9.0.8, FTB Library ≥2001.2.1, **FTB Teams ≥2001.1.4** | Item Filters |
| FTB Teams 2001.3.2 | Forge ≥47.1.47, Architectury ≥9.1.12, FTB Library ≥2001.2.0 | — |
| FTB Library 2001.2.13 | Forge ≥47.3, Architectury ≥9.0.8 | FTB Quests ≥2001.4.8 |
| Architectury 9.2.14 | Forge ≥46 | — |
| Simply Swords 1.70.2 | Architectury ≥9.2.14, Fzzy Config ≥0.6.6, Simply Tooltips ≥0.1 (client) | Better Combat |
| Basic Weapons 1.2.4 | Forge ≥47 | — |
| CompatLink 1.1.2 | Forge ≥47.4 | Epic Fight ≥20.14 |

## 3. Startup tests — dedicated server, isolated Forge 47.4.28 install (not the project's `run/`)

Each case: fresh world, only the listed jars, start until `Done`, then `stop`. Headless: no client was run.

| # | Mods | Started | Crashes | SAO+/mod ERROR lines |
|---|---|---|---|---|
| 00 | none (baseline) | yes | 0 | 2 (vanilla worldgen "setBlock in a far chunk") |
| 01 | Epic Fight | yes | 0 | 0 |
| 02 | Pufferfish's Skills | yes | 0 | 0 |
| 03 | JAUML + RPG Attribute System | yes | 0 | 0 |
| 04 | Architectury | yes | 0 | 0 |
| 05 | Architectury + FTB Library + Teams + Quests | yes | 0 | 0 |
| 06 | Architectury + Fzzy Config + KFF + Simply Swords | yes | 0 | 0 |
| 07 | Basic Weapons | yes | 0 | 0 |
| 08 | CompatLink alone | yes | 0 | 0 |
| 09 | Epic Fight + CompatLink | yes | 0 | 0 |
| 10 | SAO+ Core alone (`saoplus-1.0.0.jar`) | yes | 0 | 0 |
| 11 | All 13 candidate/library jars, without Core | yes | 0 | 0 |
| 12 | All of the above **with SAO+ Core** | yes | 0 | 0 |

Forge's debug log recognised all 14 mods in case 12 (including `saoplus`). **VERIFIED IN GAME** for server startup and coexistence only.

Warnings seen (none fatal):
- Epic Fight: `defineId called for …` (14, also present when Epic Fight runs alone) and a mixin `@Final field … should be final`.
- Simply Swords mixins reference classes of optional mods not installed (Better Combat, Lootr).
- Forge's own `fmlcore`/`javafmllanguage` "missing mods.toml" (benign).

## 4. Not established — do not assume

- **Client behavior**: no client was launched against these mods. Epic Fight animations, input, HUD, Simply Tooltips (client-side) and any GUI are **NOT VERIFIED**.
- **Public APIs and integration points** (Phase 2 step 5–6 of the roadmap): not inspected. No event, capability or identifier of Epic Fight, RAS or Pufferfish's Skills was read or tested. Weapon identifiers are **not** known.
- **Weapon animations for Simply Swords 1.70.2 / Basic Weapons 1.2.4 under Epic Fight**: not established. CompatLink is data-driven (JSON movesets) and starts with Epic Fight, but the community "Simply Swords Epic Fight Compat" data pack found targets Simply Swords 1.56.0, and "Epic Fight Weapons Compat" targets NeoForge 1.21.1 — neither verified for this target. CompatLink on its own contributes no movesets.
- **Gameplay**: RAS levels/attributes, Pufferfish's skill trees, FTB quest execution, and saving/loading worlds with these mods are untested.
- **Persistence/sync of SAO+ data alongside these mods** beyond startup is untested.
- **Newer versions exist** (Epic Fight 20.15.x betas, RAS 4.3.0/5.x, JAUML 3.0.0); the pinned versions were tested as requested. Pufferfish's Skills 0.19.2 is published as beta.
- **Licensing** was not reviewed (Pufferfish and Simply Swords: custom licenses; CompatLink: All Rights Reserved).

## 5. Provisional assessment (not a decision)

| Candidate | Status |
|---|---|
| Epic Fight, Pufferfish's Skills, RAS (+JAUML), Architectury, FTB trio | Start and coexist on a dedicated server; viable for continued API and client testing. |
| Simply Swords | Starts, but pulls three extra libraries including Kotlin for Forge; weapon animation compatibility unproven. |
| Basic Weapons | Starts with no extra dependencies; Epic Fight moveset coverage unproven. |
| CompatLink | Starts alone and with Epic Fight; value depends on movesets that were not located for these weapon mods. |

No candidate has been rejected, and none has been approved.

## 6. Client round (2026-11-09) — isolated Forge 47.4.28 client + dedicated server, all 14 mods + `saoplus-1.0.0.jar`

Setup: official Forge installer into an isolated `.minecraft`; launch arguments generated from the Forge/vanilla version JSONs (offline user `TestA`, correct offline UUID); vanilla assets reused read-only from the Gradle cache; auto-connect with `--quickPlayMultiplayer`; server commands fed through the server console. `New World`, `run/` and any production data were not used.

| # | Required test | Result | Evidence |
|---|---|---|---|
| 1 | Client starts without errors | **VERIFIED IN GAME** with caveats | Reached the world; client log has 1 ERROR (Epic Fight web server, see below) and 126 WARN. First launch attempts failed because of my launcher (vanilla jar left on the classpath); fixed. |
| 2 | Joins the dedicated server | **VERIFIED IN GAME** | `TestA joined the game`, three separate sessions. |
| 3 | Interface and screens | **PARTIALLY VERIFIED** | One window-only screenshot (since deleted) showed the in-world render with RAS's XP bar ("0/140 XP", level 1), its key hint and an FTB sidebar button. No screen was opened (no way to send input). Holographic/character screens, Pufferfish tree UI and FTB Quests UI are **NOT VERIFIED**. |
| 4 | Animations | **NOT VERIFIED** | Epic Fight loaded with compute-shader support reported; no animation was observed (no way to attack or play). |
| 5 | Simply Tooltips | **NOT VERIFIED (visual)** | Loaded and read its config (`SimplyTooltips config loaded`); no tooltip was hovered. |
| 6 | Weapons and equipment | **VERIFIED IN GAME (server side)** | `item replace` placed `simplyswords:diamond_claymore` (main hand), `basicweapons:iron_dagger` (off hand) and `minecraft:iron_helmet`; `data get entity` read them back. Attack/animation behaviour **NOT VERIFIED**. |
| 7 | World save and load | **VERIFIED IN GAME** | `save-all flush`, `stop`, restart: inventory and capabilities identical. |
| 8 | Disconnect and reconnect | **VERIFIED IN GAME** | Client process killed (hard disconnect) and relaunched: same inventory; also after a full server restart. |
| — | Capability coexistence | **VERIFIED IN GAME** | `data get entity TestA ForgeCaps` lists `saoplus:player_data`, `rpg_attribute_system:player_variables` and `epicfight:skill_cap` together. |
| — | Logs | Server: 0 ERROR in both sessions, no crash reports. Client: 1 ERROR. | See "Negative results". |

### Negative / unresolved results (kept, not suppressed)
- Epic Fight client: `Failed at connecting Epic Fight web server: java.net.ConnectException` and `Failed at initializing Authentication provider: ClassNotFoundException … epicskins.user.AuthenticationHelper(Impl)`. It concerns the EpicSkins online service; not investigated further. Epic Fight's earlier warnings (`defineId called for …` ×14, mixin `@Final field … should be final`) are unchanged and **not resolved**.
- Simply Swords mixin warnings are unchanged: missing optional targets Better Combat (`net.bettercombat.network.ServerNetwork`), Spell Engine (`net.spell_engine.client.input.SpellHotbar`, client only) and Lootr. They are probably harmless because those mods are optional, but that is not proven; they stay listed as potential incompatibilities.
- FTB Library: `can't read …\config\sidebar_buttons.json` (first run, file not yet created — not confirmed).
- Mojang/other warnings: `Missing sound for event minecraft:item.goat_horn.play`, a shader sampler warning (`rendertype_entity_translucent_emissive`), many `ForgeConfigSpec` "corrected from null" lines (first-run config creation).
- A launcher artifact of mine (fake UUID) caused two FTB Teams errors ("Local player id … was not found in the known players list"); they disappeared with the correct offline UUID, so they are not a mod defect — but they show FTB Teams requires the client UUID to match the server's.

## 7. API inspection — evidence from the jars (`javap`, `mods.toml`, archive entries, runtime logs)

### Epic Fight 20.14.17 — VERIFIED BY CODE
- **Forge events** (`yesman.epicfight.api.forgeevent`): `BattleModeSustainableEvent`, `ChangePlayerModeEvent`, `EntityPatchRegistryEvent`, `EntityStunEvent` (damage source, stunned patch, `StunType`), `InitAnimatorEvent`, `InnateSkillChangeEvent`, `SkillBuildEvent`, `SkillLootTableRegistryEvent`, `WeaponCapabilityPresetRegistryEvent` (mod bus; `getTypeEntry()` returns `Map<ResourceLocation, Function<Item, CapabilityItem.Builder>>`).
- **Player combat events** via `PlayerEventListener.addEventListener(EventType<T>, UUID, Consumer<T>)` on a `PlayerPatch`: `DEAL_DAMAGE_EVENT_ATTACK/HURT/DAMAGE`, `TAKE_DAMAGE_EVENT_*`, `BASIC_ATTACK_EVENT`, `ANIMATION_BEGIN/END_EVENT`, `ATTACK_ANIMATION_END_EVENT`, `ATTACK_PHASE_END_EVENT`, `SKILL_CAST/CANCEL/CONSUME_EVENT`, `STAMINA_CONSUME_EVENT`, `DODGE_SUCCESS_EVENT`, `PLAYER_KILLED_EVENT`, `MODIFY_DAMAGE_EVENT`, `MODIFY_ATTACK_SPEED_EVENT`, among others. `DealDamageEvent` exposes `getTarget()`, `getDamageSource()` (`EpicFightDamageSource`), `getAttackDamage()`, `getForgeEvent()`; `DodgeSuccessEvent` exposes `getDamageSource()`, `getLocation()`; `SkillCastEvent` exposes `getSkillContainer()` and can veto execution (`setSkillExecutable`).
- **Entry points**: `EpicFightCapabilities.getServerPlayerPatch(ServerPlayer)`, `getPlayerPatchAsOptional(Entity)`, `getItemStackCapability(ItemStack)`, capabilities `CAPABILITY_ENTITY/ITEM/SKILL/PROJECTILE`.
- **Animations**: `AnimationManager.getInstance()`, `byKey(ResourceLocation)`, and mod-bus `AnimationManager.AnimationRegistryEvent.newBuilder(String, Consumer<AnimationBuilder>)`.
- **Skills**: registry `SkillManager.SKILL_REGISTRY_KEY`, `getSkill(String)`, `getSkillRegistry()`.
- **Weapon mapping by data**: datapack path `data/<namespace>/capabilities/weapons/<item>.json` with `{"type":"epicfight:<preset>","attributes":{"common":{"impact":…,"max_strikes":…}}}` (e.g. `minecraft:iron_sword` → `epicfight:sword`), plus `capabilities/weapons/item_keyword/*.json` with regex lists (`.*_sword`, `.*_longsword`, …).
- **Presets** in `WeaponCapabilityPresets`: AXE, HOE, PICKAXE, SHOVEL, SWORD, SPEAR, GREATSWORD, UCHIGATANA, TACHI, LONGSWORD, DAGGER, FIST, BOW, CROSSBOW, TRIDENT, SHIELD. No hammer/mace preset exists (found by class fields; not tested at runtime beyond the two below).
- The package layout also contains the `api.event` hook classes (`EpicFightHooks` has no public members).
- **VERIFIED IN GAME**: `epicfight:skill_cap` is attached to the player.

### RPG Attribute System 4.2.6 ("Remnants") — VERIFIED BY CODE, persistence VERIFIED IN GAME
- **Public API** `tn.nightbeam.ras.api.RasApi` (all static): `isAvailable()`, `getLevel(Player)`, `getCombatSnapshot(Player)` → record `CombatSnapshot(rpgLevel, maxHealth, attackDamage, armor, armorToughness, movementSpeed)`, `respec(Player[, RespecOptions])`, `applyTemplate(Player, String[, boolean])`. No Forge event classes for level-up were found in the jar.
- **Persistence**: its own capability `RpgAttributeSystemModVariables.PLAYER_VARIABLES_CAPABILITY` (`PlayerVariables`: `Level`, `SparePoints`, `totalXp`, `attributes`, `attributePoints`, `playerUnlockedAttributes`, `lastRespecEpochMs`; `writeNBT/readNBT`), synced by `syncPlayerVariables`. Runtime NBT key `rpg_attribute_system:player_variables` showed `Level: 0.0`, `SparePoints: 1.0`, `nextevelXp: 140.0` and `attribute_1…attribute_8` (default values observed: 20, 1, 4, …, 0.1).
- **Attributes**: generic slots `RpgAttributeSystemModAttributes.ATTRIBUTE_1…ATTRIBUTE_10` plus `RPG_LEVEL`, defined by config (`AttributeData`), not by fixed names. The six candidate GDD attributes (Strength, Vitality, …) have **not** been mapped to slots.
- Other findings: commands, items (`ScrollOfRebirthItem`, `TomeOfAscensionItem`), level-up reward checks and item-requirement locking exist as internal classes; none were exercised.

### Pufferfish's Skills 0.19.2 — VERIFIED BY CODE
- `net.puffish.skillsmod.api.SkillsAPI` (static): `registerSkillUnlockEvent`, `registerSkillLockEvent`, `registerNewPointEvent`, `registerReward(ResourceLocation, RewardFactory)`, `registerExperienceSource(ResourceLocation, ExperienceSourceFactory)`, `updateExperienceSources(ServerPlayer, …)`, `updateRewards(ServerPlayer, …)`, `openScreen`, `getCategory/streamCategories/streamUnlockedCategories`, `exportPlayerData/importPlayerData(ServerPlayer, CompoundTag)`.
- `Category`: `unlock/lock/isUnlocked/resetSkills/erase`, `getSkill/streamSkills/streamUnlockedSkills`, points API (`addPoints`, `setPoints`, `getPointsLeft`, …), `getExperience()`, `getExchange()`.
- Implication (to be tested): custom rewards and experience sources can be registered from outside, so SAO+ rules (e.g. specialization effects) could be expressed as `Reward`s. This was **not** tested at runtime.

### Simply Swords 1.70.2 — VERIFIED BY CODE, items VERIFIED IN GAME
- Namespace `simplyswords`; 160 `item.simplyswords.*` language keys (e.g. `diamond_claymore`, `diamond_longsword`, `diamond_katana`, `diamond_rapier`, `diamond_spear`, `diamond_greataxe`, `diamond_greathammer`, `diamond_halberd`, `diamond_scythe`, `diamond_chakram`, `diamond_twinblade`, `diamond_warglaive`, `diamond_sai`, `diamond_cutlass`, `diamond_glaive`, …).
- 133 `data/simplyswords/weapon_attributes/*.json` files in **Better Combat's format** (`"parent": "bettercombat:claymore"`, `"category": "claymore"`, `"two_handed"`, `"pose"`). Counts by category: claymore 20, sword 13, sickle 10, chakram 9, battlestaff 9, greathammer 8, halberd 8, katana 7, warglaive 7, soul_knife 6, greataxe 5, scythe 4, glaive 3, trident 2, rapier 2, mace 1 (+ ones inheriting `bettercombat:trident/rapier/glaive/heavy_axe`).
- **No Epic Fight data or references at all** in the jar (0 entries). Its own weapon data is therefore inert for Epic Fight.
- Runtime item NBT carries `SimplySwordsComponents → simplyswords:weapon_implicit {implicit_id, weapon_type: "simplyswords:claymore", value}`, i.e. a weapon-type identifier on the stack. **VERIFIED IN GAME** (`data get entity`).
- Also ships LevelZ item data (`data/levelz/item`, 386 files) and Mythic Metals tags; unused here.

### Basic Weapons 1.2.4 — VERIFIED BY CODE, items VERIFIED IN GAME
- Namespace `basicweapons`; 42 items = 7 materials × {club, dagger, glaive, hammer, quarterstaff, spear}; item classes `BasicWeaponItem`, `BasicWeaponSweeplessItem`, `ClubItem`, `DaggerItem`, `GlaiveItem`, `HammerItem`, `QuarterstaffItem`; 43 `weapon_attributes` JSONs (Better Combat format); no Epic Fight references; no NBT components on the stack (`basicweapons:iron_dagger` had only `Damage`).

### CompatLink 1.1.2 — VERIFIED BY CODE and VERIFIED IN GAME
- It is a mapping loader for Epic Fight: it ships **no weapon data**. It reads `config/compatlink/mappings/*.json` (and datapack mappings) of the form `{"format":1,"backend":"epicfight","weapons":[{"target":"<item id>","preset":"epicfight:<preset>","params":{…},"priority":n,"trail":…}],"entities":[{"target","preset"}]}`; an example file is generated disabled (`_example.json.disabled`, `cataclysm:ignis` → `epicfight:wither`). `auto_trails` setting adds swing trails.
- **Test**: with a test file mapping `simplyswords:diamond_claymore` → `epicfight:greatsword` and `basicweapons:iron_dagger` → `epicfight:dagger`, the server logged `CompatLink: 0 entity mappings, 2 weapon mappings applied (0 failed)` and the client generated 2 trail skins. This shows Epic Fight's backend accepted the mappings. Whether the animations then play correctly was **not** observed.
- Because Epic Fight itself reads `data/<namespace>/capabilities/weapons/*.json`, the same mapping may be achievable with a datapack without CompatLink; that alternative was **not** tested.

## 8. Summary of results

| Area | Positive | Negative / limits | Pending |
|---|---|---|---|
| Startup and coexistence | 14 mods + Core start on server and client; no crashes; capabilities coexist | Epic Fight and Simply Swords warnings remain; EpicSkins connection error | Behaviour under load / multiple clients |
| Persistence | Inventory and all three capabilities survive restart and reconnect | — | Persistence of mastery data in this mod mix |
| Epic Fight API | Combat, dodge, skill and stun events; preset registry; data-driven weapon mapping confirmed in classes | No hammer/mace preset | Runtime use of events; animation playback |
| RAS API | `RasApi` level/snapshot, own persisted capability, configurable attribute slots | No level-up event found; attribute slot ↔ GDD attribute mapping unknown | Config of slots; reading attributes from Core |
| Pufferfish API | Custom rewards/exp sources/events, category and point control, export/import data | — | Everything at runtime |
| Simply Swords / Basic Weapons | Item ids and weapon types identified; items work in inventory | Data targets Better Combat, not Epic Fight | Animations with Epic Fight mapping |
| CompatLink | Applies user-written mappings (2/2 accepted) | Ships no mappings | Visual/animation check; datapack alternative |

## 9. Still pending
- Visual checks: character/holographic screens, Pufferfish and FTB Quests UIs, Simply Tooltips, Epic Fight animations and attacks (need a person at the keyboard or a way to send input).
- Multiple simultaneous clients; dedicated-server tests under load.
- Using any of these APIs from Core code (none is a compile dependency yet).
- Licensing review.
- Decision on which candidates enter the Core build.

Test artifacts (jars, isolated server/client, logs) are in the session scratchpad (`…\scratchpad\p2\`), outside the repository.

Test artifacts live in the session scratchpad (`…\scratchpad\p2\`: `downloads\`, `server-base\`, `results\`), outside the repository. The user's `eula.txt` acceptance was copied into the isolated server directory.

## Update 2026-11-09: Epic Fight used by the Core
Epic Fight 20.14.17 is now a compile-only dependency of the Core and an optional runtime dependency. API used: EpicFightCapabilities.getItemCapability, CapabilityItem.getWeaponCategory, WeaponCategories, EpicFightDamageSource. Compiles and loads against 20.14.17; other versions are untested.
