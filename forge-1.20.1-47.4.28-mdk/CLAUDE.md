# Project instructions

- Minecraft 1.20.1, Forge 47.4.28, Java 17. Inspect nearby files in `src/main/java` and `src/main/resources` for established patterns.
- Keep changes scoped; inspect nearby code before editing and avoid unrelated refactors.
- Verify Java changes with `.\gradlew.bat compileJava`; use `.\gradlew.bat build` for broader checks.
- Don't launch the game or run data generation unless the task needs it.
- Prefer concise answers. Summarize changed files and verification; report blockers plainly.

## SAO+ project context

- SAO+ is a multiplayer Minecraft RPG server project inspired by Sword Art Online. The planned in-house mods are SAO+ RPG and SAO+ Combat; CurseForge manages the modpack.
- The project is pinned to Minecraft Java 1.20.1, Forge 47.4.28, and Java 17. Use IntelliJ IDEA and the Gradle Wrapper.
- For RPG system design, follow decisions confirmed in the GDD; do not invent unconfirmed rules, values, or formulas.
- Use only Minecraft Forge 1.20.1 APIs. Do not apply Fabric, NeoForge, Paper, or Bukkit patterns unless the target changes.
- Do not apply Fabric, NeoForge, Paper, or Bukkit implementation patterns unless the user explicitly changes the target.
- Inspect helper scripts before running them. Do not launch a server, run the game, generate data, or invoke a broad build unless the task calls for it.
