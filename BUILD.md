<!--
  Modified by yigemingzii, July 2026
  - Added Minecraft 1.21.11 and 26.2 build instructions
  - Added Minecraft 26.3 build instructions and CommandAPI 12.1.0 requirement
-->
# Build introduction

Here is a simple introduction lead you to build this project

## Build NMS Dependencies

### Minecraft 1.20.x - 1.21.x (Spigot BuildTools)

Mojang does not allow anyone to publish the remapped NMS jar to any public repository,
so you need to build it yourself

1. Download [BuildTools](https://www.spigotmc.org/wiki/buildtools/)
2. Execute `java -jar BuildTools.jar --rev <version> --remapped` for each required version. Minecraft 1.21.11 installs `1.21.11-R0.2-SNAPSHOT`; older versions use `R0.1-SNAPSHOT`.

### Minecraft 26.x (Paper Server Jar)

Starting from Minecraft 26.1, Mojang ships the server unobfuscated, so BuildTools is no longer needed.
Paper also no longer publishes spigot remapped-mojang artifacts for 26.x.
Instead, the `fakeplayer-v26_1_2`, `fakeplayer-v26_2`, and `fakeplayer-v26_3` modules compile against Paper server jars directly. Building 26.x requires JDK 25.

1. Download the Paper paperclip jars from [papermc.io/downloads/paper](https://papermc.io/downloads/paper). The 26.2 module currently targets build 68 Beta. The experimental 26.3 module targets [build 41 Alpha](https://fill-data.papermc.io/v1/objects/2b77166ee61886a9bc9ab33dc9e4847fa3538b36d9ba6e5f2fa7ed90973aa748/paper-26.3-41.jar) (`paper-api:26.3.build.41-alpha`). Save it as `lib/paper-26.3-server.jar`.
2. Run each paperclip jar once to extract the server jar:
   - `java -jar lib/paper-26.1.2-server.jar --help`
   - `java -jar lib/paper-26.2-server.jar --help`
   - `java -jar lib/paper-26.3-server.jar --help`

3. Confirm these extracted files exist:
   - `versions/26.1.2/paper-26.1.2.jar`
   - `versions/26.2/paper-26.2.jar`
   - `versions/26.3/paper-26.3.jar` (the extracted server jar, not the downloaded paperclip)

4. Build the modules: `mvn compile -pl fakeplayer-v26_1_2,fakeplayer-v26_2,fakeplayer-v26_3 -am`

