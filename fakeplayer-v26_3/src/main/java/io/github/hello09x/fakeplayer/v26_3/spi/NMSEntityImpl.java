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
package io.github.hello09x.fakeplayer.v26_3.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSEntity;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

public class NMSEntityImpl implements NMSEntity {

    private final Entity handle;

    public NMSEntityImpl(@NotNull org.bukkit.entity.Entity entity) {
        this.handle = ((CraftEntity) entity).getHandle();
    }

    public Entity getHandle() {
        return handle;
    }

}
