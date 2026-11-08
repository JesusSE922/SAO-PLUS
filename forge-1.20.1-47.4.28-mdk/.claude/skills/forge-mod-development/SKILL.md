---
name: forge-mod-development
description: Develop and maintain this Minecraft 1.20.1 Forge mod. Use when implementing or reviewing mod code, registries, events, networking, capabilities, configuration, or resources.
---

# Forge mod development

## Project constraints

- Target Minecraft 1.20.1, Forge 47.4.28, and Java 17.
- Follow the existing Gradle configuration and inspect nearby code in `src/main/java` and `src/main/resources` before changing behavior.
- Keep changes focused. Preserve existing naming, formatting, mod IDs, and registration patterns.
- Do not launch the game or run data generation unless the task specifically requires it.
- After Java changes, run `.\gradlew.bat compileJava`. Use `.\gradlew.bat build` when broader verification is warranted.

## Forge implementation patterns

- Register blocks, items, creative tabs, and similar content with `DeferredRegister` and `RegistryObject`, attached to the mod event bus.
- Keep mod lifecycle setup on the mod event bus and gameplay events on `MinecraftForge.EVENT_BUS`; use the correct bus and physical side for each subscriber.
- Keep common/server code free of references to client-only classes. Isolate client setup and client event handlers with Forge's side-aware patterns.
- For network packets, define and validate the protocol and direction, register packet handlers, and enqueue game-state work on the appropriate thread. Keep authoritative player/game state changes on the server and synchronize only what clients need.
- For capabilities and player data, follow the existing attach, serialize, clone, and synchronization flows. Handle absent capabilities explicitly rather than hiding failures behind defaults.
- Keep resource identifiers namespaced with the mod ID. When editing `mods.toml` or other expanded resources, preserve the property placeholders and metadata conventions in `build.gradle`.
- Avoid adding dependencies or changing mappings unless the requested feature requires it.

## Workflow

1. Trace the relevant existing implementation and resource definitions before editing.
2. Make the smallest complete change, including registration, side handling, synchronization, and resources where applicable.
3. Run the targeted Gradle verification for the changed code and report any failure clearly.
