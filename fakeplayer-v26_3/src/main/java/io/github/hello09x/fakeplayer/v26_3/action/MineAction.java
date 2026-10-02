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
package io.github.hello09x.fakeplayer.v26_3.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

public class MineAction extends TraceAction {

    private final Current current = new Current();

    public MineAction(ServerPlayer player) {
        super(player);
    }

    @Override
    @SuppressWarnings("resource")
    public boolean tick() {
        var hit = this.getTarget();
        if (hit == null) {
            return false;
        }

        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        if (current.freeze > 0) {
            current.freeze--;
            return false;
        }

        var blockHit = (BlockHitResult) hit;
        var pos = blockHit.getBlockPos();
        var side = blockHit.getDirection();

        if (player.blockActionRestricted(player.level(), pos, player.gameMode.getGameModeForPlayer())) {
            return false;
        }

        if (current.pos != null && player.level().getBlockState(current.pos).isAir()) {
            current.pos = null;
            return false;
        }

        var state = player.level().getBlockState(pos);
        var broken = false;
        if (player.gameMode.getGameModeForPlayer().isCreative()) {
            player.gameMode.handleBlockBreakAction(
                    pos,
                    START_DESTROY_BLOCK,
                    side,
                    player.level().getMaxY(),
                    -1
            );
            current.freeze = 5;
            broken = true;
        } else if (current.pos == null || !current.pos.equals(pos)) {
            if (current.pos != null) {
                player.gameMode.handleBlockBreakAction(
                        current.pos,
                        ABORT_DESTROY_BLOCK,
                        side,
                        player.level().getMaxY(),
                        -1
                );
            }

            player.gameMode.handleBlockBreakAction(
                    pos,
                    START_DESTROY_BLOCK,
                    side,
                    player.level().getMaxY(),
                    -1
            );

            if (!state.isAir() && current.progress == 0) {
                state.attack(player.level(), pos, player);
            }

            if (!state.isAir() && state.getDestroyProgress(player, player.level(), pos) >= 1) {
                current.pos = null;
                broken = true;
            } else {
                current.pos = pos;
                current.progress = 0;
            }
        } else {
            current.progress += state.getDestroyProgress(player, player.level(), pos);
            if (current.progress >= 1) {
                player.gameMode.handleBlockBreakAction(
                        pos,
                        STOP_DESTROY_BLOCK,
                        side,
                        player.level().getMaxY(),
                        -1
                );
                current.pos = null;
                current.freeze = 5;
                broken = true;
            }
            player.level().destroyBlockProgress(-1, pos, (int) (current.progress * 10));
        }

        player.resetLastActionTime();
        player.swing(InteractionHand.MAIN_HAND, player.getMainHandItem().getAttackAnimation(), false);
        return broken;
    }

    @Override
    public void inactiveTick() {
        stop();
    }

    @Override
    @SuppressWarnings("resource")
    public void stop() {
        if (current.pos == null) {
            return;
        }

        player.level().destroyBlockProgress(-1, current.pos, -1);
        player.gameMode.handleBlockBreakAction(
                current.pos,
                ABORT_DESTROY_BLOCK,
                Direction.DOWN,
                player.level().getMaxY(),
                -1
        );
        current.pos = null;
        current.freeze = 0;
        current.progress = 0;
    }

    private static class Current {

        @Nullable
        public BlockPos pos;

        public float progress;

        public int freeze;

    }

}
