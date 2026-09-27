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
package io.github.hello09x.fakeplayer.v26_3.action.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Tracer {

    public static @Nullable HitResult rayTrace(
            @NotNull Entity source,
            float partialTicks,
            double reach,
            boolean fluids
    ) {
        var blockHit = rayTraceBlocks(source, partialTicks, reach, fluids);
        double maxSqDist = reach * reach;
        if (blockHit != null) {
            maxSqDist = blockHit.getLocation().distanceToSqr(source.getEyePosition(partialTicks));
        }
        EntityHitResult entityHit = rayTraceEntities(source, partialTicks, reach, maxSqDist);
        return entityHit == null ? blockHit : entityHit;
    }

    @SuppressWarnings("resource")
    public static @Nullable BlockHitResult rayTraceBlocks(
            @NotNull Entity source,
            float partialTicks,
            double reach,
            boolean fluids
    ) {
        var pos = source.getEyePosition(partialTicks);
        var rotation = source.getViewVector(partialTicks);
        var reachEnd = pos.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
        return source.level().clip(new ClipContext(pos, reachEnd, ClipContext.Block.OUTLINE, fluids ?
                ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, source));
    }

    public static @Nullable EntityHitResult rayTraceEntities(
            @NotNull Entity source,
            float partialTicks,
            double reach,
            double maxSqDist
    ) {
        var pos = source.getEyePosition(partialTicks);
        var reachVec = source.getViewVector(partialTicks).scale(reach);
        var box = source.getBoundingBox().expandTowards(reachVec).inflate(1);
        return rayTraceEntities(source,
                                pos,
                                pos.add(reachVec),
                                box,
                                e -> !e.isSpectator() && e.isPickable(),
                                maxSqDist);
    }

    public static @Nullable EntityHitResult rayTraceEntities(
            @NotNull Entity source,
            @NotNull Vec3 start,
            @NotNull Vec3 end,
            @NotNull AABB box,
            @NotNull Predicate<Entity> predicate,
            double maxSqDistance
    ) {
        @SuppressWarnings("resource")
        var world = source.level();
        double targetDistance = maxSqDistance;
        Entity target = null;
        Vec3 targetHitPos = null;
        for (Entity current : world.getEntities(source, box, predicate)) {
            var currentBox = current.getBoundingBox().inflate(current.getPickRadius());
            var currentHit = currentBox.clip(start, end);
            if (currentBox.contains(start)) {
                if (targetDistance >= 0) {
                    target = current;
                    targetHitPos = currentHit.orElse(start);
                    targetDistance = 0;
                }
            } else if (currentHit.isPresent()) {
                var currentHitPos = currentHit.get();
                var currentDistance = start.distanceToSqr(currentHitPos);
                if (currentDistance < targetDistance || targetDistance == 0) {
                    if (current.getRootVehicle() == source.getRootVehicle()) {
                        if (targetDistance == 0) {
                            target = current;
                            targetHitPos = currentHitPos;
                        }
                    } else {
                        target = current;
                        targetHitPos = currentHitPos;
                        targetDistance = currentDistance;
                    }
                }
            }
        }
        return target == null ? null : new EntityHitResult(target, targetHitPos);
    }
}
