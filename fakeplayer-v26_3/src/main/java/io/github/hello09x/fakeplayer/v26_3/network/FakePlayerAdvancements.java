/*
 * Copyright 2026 yigemingzii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by yigemingzii, September 2026 for Minecraft 26.3 support.
 */
package io.github.hello09x.fakeplayer.v26_3.network;

import com.mojang.datafixers.DataFixer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.nio.file.Path;

public class FakePlayerAdvancements extends PlayerAdvancements {

    public FakePlayerAdvancements(
            DataFixer datafixer,
            PlayerList playerlist,
            ServerAdvancementManager manager,
            Path path,
            ServerPlayer player
    ) {
        super(datafixer, playerlist, manager, path, player);
        this.save();
    }

    @Override
    public boolean award(AdvancementHolder advancementholder, String s) {
        return false;
    }

    @Override
    public void flushDirty(ServerPlayer entityplayer, boolean flag) {

    }

    @Override
    public AdvancementProgress getOrStartProgress(AdvancementHolder advancement) {
        return new AdvancementProgress();
    }

    @Override
    public boolean revoke(AdvancementHolder advancement, String s) {
        return false;
    }

    @Override
    public void save() {
    }

    @Override
    public void setPlayer(ServerPlayer player) {
    }

    @Override
    public void setSelectedTab(AdvancementHolder advancement) {
    }

    @Override
    public void clearTriggers() {

    }

}
