# SAO+ Core — Repository Audit (Phase 0)

Date: 2026-11-09 · Branch: `fix/player-capability-clone` · HEAD: `322daae` ("post test")

Verification labels: **VERIFIED IN GAME**, **VERIFIED BY CODE**, **NOT VERIFIED**.
No gameplay code was modified during this audit; this file is the only change.

## 1. Environment

| Item | Value | Source |
|---|---|---|
| Minecraft | 1.20.1 (`[1.20.1,1.21)`) | `gradle.properties` |
| Forge | 47.4.28 (`[47,)`) | `gradle.properties` |
| Java | 17 toolchain (running 17.0.21, Microsoft) | `build.gradle` |
| ForgeGradle | `[6.0,6.2)`, Gradle 8.8 | `build.gradle`, wrapper |
| Mappings | official 1.20.1 | `gradle.properties` |
| Mod | `saoplus` "SAO+ Core" 1.0.0, group `com.saoplus.core`, license "All Rights Reserved" | `gradle.properties` |
| Dependencies | Forge/Minecraft only; JUnit 5.10.2 (test scope, added in `322daae`) | `build.gradle`, `mods.toml` |

## 2. Repository and Git state

- Git root is `SAO-PLUS/`; the project is `forge-1.20.1-47.4.28-mdk/`. The root contains only `.gitignore` (`/BACKUPS/`).
- Branches: `master` (= `origin/master`, `f2f0352`) and `fix/player-capability-clone` (= origin, 2 commits ahead: `2e76cc9`, `322daae`). Not merged into `master`.
- Working tree was clean at audit start (before this file).
- `322daae` committed the `LazyOptional` renewal fix, the Weapon Mastery data model, JUnit setup and `.mcp.json`.
- Documentation present: `README.txt` (short), `CLAUDE.md`, `.claude/skills/forge-mod-development/SKILL.md`.
  Leftover MDK files: `CREDITS.txt`, `changelog.txt` (Forge changelog), `LICENSE.txt`.
- **No GDD is in the repository.** Gameplay decisions cannot be checked against it from here.
- None of the roadmap documents existed before this one (`DEVELOPMENT_ROADMAP.md`, `DEPENDENCY_COMPATIBILITY.md`, `ARCHITECTURE.md`, `IMPLEMENTATION_STATUS.md`, `TESTING_REPORT.md`, `TECHNICAL_DECISIONS.md`).

## 3. Source inventory (`src/main/java/com/saoplus/core`)

| Class | Function |
|---|---|
| `SaoPlus` | `@Mod` entry; calls `ModNetwork.register()`; owns the logger. |
| `PlayerData` | Untyped `CompoundTag` plus a typed `WeaponMasteryData`, stored under key `weapon_mastery` (omitted when empty). |
| `PlayerDataCapability` | `Capability<PlayerData>` token; registered on the MOD bus. |
| `PlayerDataProvider` | `ICapabilitySerializable<CompoundTag>`; renews its `LazyOptional` when invalidated. |
| `PlayerDataEvents` | Attach (Player only) with invalidation listener; `Clone` copy (`reviveCaps`/`invalidateCaps` on death); sync on login, respawn, dimension change. |
| `PlayerDataSyncPacket` | Full-snapshot NBT, server → client; client handler runs via `enqueueWork` + `DistExecutor`. |
| `ModNetwork` | `saoplus:main` channel, protocol `"1"`, one message (id 0, `PLAY_TO_CLIENT`). |
| `WeaponMasteryData` / `WeaponMasteryRecord` | Storage-only mastery model keyed by open `ResourceLocation` family id. No rules, ranks or formulas. |

Tests: `src/test/java/.../WeaponMasteryPersistenceTest.java` — 9 NBT-level unit tests.

No registries, items, blocks, commands, config, client code, resources (beyond `pack.mcmeta`/`mods.toml`) or integrations exist.

## 4. Verification status

| Claim | Status | Basis |
|---|---|---|
| `compileJava` and `test` succeed (9/9 tests) | **VERIFIED BY CODE** | Run in this audit: BUILD SUCCESSFUL. |
| `.\gradlew.bat build` (full) | **NOT VERIFIED** in this audit | Not run; user reported a final build earlier. |
| Client/server sync, save/reload, death, dimension travel | **VERIFIED IN GAME** — reported by the user for Phase 1; not re-executed here | User report. |
| `LazyOptional` renewal fix | **VERIFIED IN GAME** — user report | User report. |
| Weapon Mastery NBT round-trip, copy, tolerance | **VERIFIED BY CODE** | Unit tests. |
| Weapon Mastery inside the real capability lifecycle (login/clone/sync) | **NOT VERIFIED** | Step 5 of the mastery plan was never done. |
| Dedicated server startup and sync | **NOT VERIFIED** | No evidence it was run. |
| Multiple simultaneous clients | **NOT VERIFIED** | No evidence. |
| Behavior with other mods installed | **NOT VERIFIED** | Phase 2 scope. |

## 5. Findings and technical risks

Priority: **H** high, **M** medium, **L** low.

1. **M — Roadmap order vs. work done.** Weapon Mastery storage (Phase 4 territory) was added before Phases 0, 2 and 3. It is storage only (no rules), but it was not authorized by this roadmap. Needs a keep/shelve decision.
2. **M — `.mcp.json` is tracked again.** `f2f0352` deliberately removed it; `322daae` re-added it. It holds a local IDEA endpoint (`127.0.0.1:64342`) and is machine-specific.
3. **M — `getCapability` re-validates after invalidation.** Forge providers normally stay invalid once invalidated. Here the entity-removal listener calls `invalidate()` and the next `getCapability` creates a fresh `LazyOptional`, so a removed entity's capability becomes usable again. It fixed the death/clone case in game, but the semantics are broader than needed. The field is also not thread-safe (acceptable if only the server thread touches it; not proven).
4. **M — Inconsistent missing-capability handling.** `ModNetwork.syncPlayerData` throws `IllegalStateException` inside login/respawn/dimension handlers, while the `Clone` handler logs and returns. A missing capability at login would break the event handler rather than being reported.
5. **M — Sync is snapshot-only and event-driven.** There is no sync after data changes and no dirty tracking. `FriendlyByteBuf.readNbt()` also limits a packet to 2 MiB, so a large `PlayerData` will fail to sync.
6. **M — Client packet handler applies data without validation.** It null-checks the player and rejects null NBT only through `requireNonNull` in `decode` (which throws). No size or schema guard. Direction is restricted to `PLAY_TO_CLIENT`, so there is no client → server path (good for authority).
7. **L — Protocol version policy.** `"1"` is accepted by both sides with exact match; no policy for bumping it when NBT layout changes.
8. **L — Dedicated-server class loading.** `PlayerDataSyncPacket` imports `net.minecraft.client.Minecraft`; the usage is wrapped in the `DistExecutor` lambda pattern, which is the standard-safe form, but it has not been run on a dedicated server.
9. **L — Test coverage gap.** Tests cover the NBT model only. Provider, events and packet have none; they require in-game or GameTest validation.
10. **L — Build leftovers.** `publishing` block points to a `mcmodsrepo` file repository; MDK default `changelog.txt`/`CREDITS.txt`; `mods.toml` has no `logoFile`, issue tracker or update URL. Harmless now.
11. **L — Documentation drift.** `CLAUDE.md` repeats the Fabric/NeoForge/Paper/Bukkit rule twice; no GDD or decision log in the repo.
12. **L — No runtime mod list recorded.** `run/mods` is empty (and `run/` is git-ignored), so the dependency candidates in Phase 2 have no recorded baseline.

## 6. Backlog (prioritized)

1. Decide the status of the Weapon Mastery model (keep as storage / shelve) and the `.mcp.json` commit.
2. Phase 1 closure tasks (below).
3. Merge decision for `fix/player-capability-clone` → `master` (user decision; not done).
4. Create `DEVELOPMENT_ROADMAP.md` and `TECHNICAL_DECISIONS.md` seeded from this audit.
5. Phase 2: dependency validation (needs exact artifacts and a test instance).
6. Phases 3–10 as defined in the roadmap; not started.

## 7. Phase 1 task list (proposal, awaiting approval)

Phase 1 behavior was already validated in game per the user. The remaining tasks close the gaps found above; none adds gameplay.

1. **Dedicated server validation** — run a dedicated server with two clients; confirm attach, login sync, respawn, dimension change, reconnect. *(NOT VERIFIED today; requires launching the game — needs approval.)*
2. **Mastery inside the lifecycle** — verify `weapon_mastery` data survives logout/login, death clone and dimension change in game, without adding debug commands. Requires deciding how to inject a test value (see open question).
3. **Missing-capability policy** — make login/respawn/dimension sync consistent with the clone handler (log vs. throw); small change in `ModNetwork`/`PlayerDataEvents`.
4. **`LazyOptional` semantics review** — decide whether renewal after invalidation should be limited to the clone/revive window; adjust `PlayerDataProvider` only if justified.
5. **Packet hardening** — document the 2 MiB limit, add safe handling of malformed NBT on decode/handle, and define the protocol-bump policy.
6. **Post-change sync hook** — design (not implement mutations) a `syncPlayerData` call point for future services.
7. **Provider/event tests** — add GameTest or equivalent coverage if approved; otherwise keep documented manual checks in `TESTING_REPORT.md`.
8. **Documentation** — create `IMPLEMENTATION_STATUS.md`, `TESTING_REPORT.md`, `TECHNICAL_DECISIONS.md`; update this audit when items close.

## 8. Decisions needed from the user

- Keep or shelve the Weapon Mastery model for now (finding 1).
- Remove `.mcp.json` from tracking again (finding 2)?
- Authorization to launch a dedicated server and clients for Phase 1 validation.
- How to inject test values for lifecycle checks without debug commands.
- Whether to merge the current branch into `master` before Phase 1 continues.
- Where the GDD lives, so decisions can be cross-checked.

Phase 1 will not start until these are answered.
