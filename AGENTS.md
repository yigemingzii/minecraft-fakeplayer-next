<!--
  Copyright 2026 yigemingzii

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
# AGENTS.md

## Build

- **No Maven wrapper** — `mvn` must be installed separately
- **Build the final jar**: `mvn package -pl fakeplayer-dist`
- **Compile a single NMS module**: `mvn compile -pl fakeplayer-v26_3 -am`
- **No tests** exist in the project

## Architecture

Multi-module Maven project producing a single Bukkit plugin jar:

```
fakeplayer-api      → SPI interfaces (NMSBridge, NMSServerPlayer, Action, …)
fakeplayer-core     → Plugin logic (commands, managers, listeners, Guice DI)
  Main.java         → JavaPlugin entrypoint, bootstraps Guice + CommandAPI registration
fakeplayer-dist     → Shade bundle: depends on core + every NMS module, shaded via maven-shade-plugin
fakeplayer-v1_20_x  \
fakeplayer-v1_21_x   } NMS implementations per Minecraft version
fakeplayer-v26_x     /
```

**ServiceLoader**: `fakeplayer-dist/src/main/resources/META-INF/services/io.github.hello09x.fakeplayer.api.spi.NMSBridge` lists every NMS provider. When adding a new version module, this file and `fakeplayer-dist/pom.xml` must both be updated.

## Two NMS dependency strategies

### 1.21.x modules (with Spigot remapping)

- Depend on `org.spigotmc:spigot:<version>-R0.x-SNAPSHOT:remapped-mojang` (built via [BuildTools](https://www.spigotmc.org/wiki/buildtools/)); 1.21.11 uses `R0.2-SNAPSHOT`
- Use `specialsource-maven-plugin` to remap: mojang → obf → spigot
- **CraftBukkit package**: `org.bukkit.craftbukkit.v1_XX_RX`
- **Cascade**: 1.21.9 and 1.21.10 share the R6 implementation; 1.21.11 is a complete R7 implementation because the CraftBukkit package changed

### 26.x modules (unobfuscated, Mojang-mapped)

- **No specialsource, no spigot jars** — Mojang removed obfuscation in 26.1
- Compile against the matching Paper server jar under `versions/<version>/paper-<version>.jar`
- Generate the jar by running the matching `lib/paper-<version>-server.jar` paperclip with `--help`
- **CraftBukkit package**: `org.bukkit.craftbukkit` — **no version suffix** (changed in 1.20.5)
- `ChatVisiblity` typo is still present in the supported 26.x APIs
- Contains complete self-contained implementation (does not cascade from a 1.21.x module)

## Adding a new NMS version module

1. Create `fakeplayer-vXX/` — mirror the file tree from an existing module of the same era
2. Update `pom.xml` `<modules>`
3. Update `fakeplayer-dist/pom.xml` — add dependency + ant-run copy target
4. Update `fakeplayer-dist/…/services/…NMSBridge` — add provider line
5. `NMSBridgeImpl.SUPPORTS` set must match `Bukkit.getMinecraftVersion()` for that version
6. Update `README.md` / `README_zh.md` / `BUILD.md`

## License (Apache 2.0)

This is an independently maintained continuation of [tanyaofei/minecraft-fakeplayer](https://github.com/tanyaofei/minecraft-fakeplayer).

- **Modified original files**: must carry "Modified by …" notice at top
- **New files**: must carry `Copyright 2026 yigemingzii` + full Apache 2.0 header
- `LICENSE.txt` must remain untouched

## Runtime dependencies

Server must have:
- [Paper](https://papermc.io) or [Purpur](http://purpurmc.org) (tests run on Purpur)
- [CommandAPI](https://commandapi.jorel.dev) plugin
- Java 21+ (26.x requires Java 25)
