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

import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.constant.ConstantPool;
import io.github.hello09x.fakeplayer.core.util.Reflections;
import io.github.hello09x.fakeplayer.v26_3.network.FakePlayerAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.level.storage.ValueInputContextHelper;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class NMSServerPlayerImpl implements NMSServerPlayer {

    private final static Field ServerPlayer$advancements = Reflections.getFirstFieldByType(
            ServerPlayer.class,
            PlayerAdvancements.class,
            false
    );

    private final ServerPlayer handle;

    private final CraftPlayer player;

    public NMSServerPlayerImpl(@NotNull Player player) {
        this.player = ((CraftPlayer) player);
        this.handle = ((CraftPlayer) player).getHandle();
    }

    public ServerPlayer getHandle() {
        return handle;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public double getX() {
        return handle.getX();
    }

    @Override
    public double getY() {
        return handle.getY();
    }

    @Override
    public double getZ() {
        return handle.getZ();
    }

    @Override
    public void setXo(double xo) {
        handle.xo = xo;
    }

    @Override
    public void setYo(double yo) {
        handle.yo = yo;
    }

    @Override
    public void setZo(double zo) {
        handle.zo = zo;
    }

    @Override
    public void doTick() {
        handle.doTick();
    }

    @Override
    public void absMoveTo(double x, double y, double z, float yRot, float xRot) {
        handle.absSnapTo(x, y, z, yRot, xRot);
    }

    @Override
    public float getYRot() {
        return handle.getYRot();
    }

    @Override
    public void setYRot(float yRot) {
        handle.setYRot(yRot);
    }

    @Override
    public float getXRot() {
        return handle.getXRot();
    }

    @Override
    public void setXRot(float xRot) {
        handle.setXRot(xRot);
    }

    @Override
    public float getZza() {
        return handle.zza;
    }

    @Override
    public void setZza(float zza) {
        handle.zza = zza;
    }

    @Override
    public float getXxa() {
        return handle.xxa;
    }

    @Override
    public void setXxa(float xxa) {
        handle.xxa = xxa;
    }

    @Override
    public void setDeltaMovement(@NotNull Vector vector) {
        handle.setDeltaMovement(new Vec3(
                vector.getX(),
                vector.getY(),
                vector.getZ()
        ));
    }

    @Override
    public boolean startRiding(@NotNull Entity entity, boolean force) {
        return handle.startRiding(new NMSEntityImpl(entity).getHandle(), force, true);
    }

    @Override
    public void stopRiding() {
        handle.stopRiding();
    }

    @Override
    public int getTickCount() {
        return handle.tickCount;
    }

    @Override
    public void drop(boolean allStack) {
        handle.drop(allStack);
    }

    @Override
    public void resetLastActionTime() {
        handle.resetLastActionTime();
    }

    @Override
    public boolean onGround() {
        return handle.onGround();
    }

    @Override
    public void jumpFromGround() {
        handle.jumpFromGround();
    }

    @Override
    public void setJumping(boolean jumping) {
        handle.setJumping(jumping);
    }

    @Override
    public boolean isUsingItem() {
        return handle.isUsingItem();
    }

    @Override
    public void disableAdvancements(@NotNull Plugin plugin) {
        if (ServerPlayer$advancements == null) {
            return;
        }

        var server = ((CraftServer) Bukkit.getServer()).getServer();
        try {
            ServerPlayer$advancements.set(
                    handle,
                    new FakePlayerAdvancements(
                            server.getFixerUpper(),
                            server.getPlayerList(),
                            server.getAdvancements(),
                            plugin.getDataFolder().getParentFile().toPath(),
                            handle
                    )
            );
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public void drop(int slot, boolean scatter, boolean setThrower) {
        var inventory = handle.getInventory();
        var item = inventory.removeItem(slot, inventory.getItem(slot).getCount());
        // 26.3 moved scatter to the extended overload and added client prediction.
        if (scatter) {
            handle.drop(item, setThrower, Prediction.SERVER_ONLY, true, true, null);
        } else {
            handle.drop(item, setThrower, Prediction.SERVER_ONLY);
        }
    }

    @Override
    public void setPlayBefore() {
        player.readExtraData(new ValueInputContextHelper(handle.registryAccess(), NbtOps.INSTANCE).empty());
    }

    @Override
    public void setupClientOptions() {
        var option = new ClientInformation(
                "en_us",
                Bukkit.getViewDistance(),
                ChatVisiblity.SYSTEM,
                false,
                ConstantPool.MODEL_CUSTOMISATION,
                HumanoidArm.RIGHT,
                false,
                true,
                ParticleStatus.MINIMAL
        );
        handle.updateOptions(option);
    }

    @Override
    public void respawn() {
        if (!this.player.isDead()) {
            return;
        }

        var packet = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
        handle.connection.handleClientCommand(packet);
    }

    @Override
    public void swapItemWithOffhand() {
        handle.connection.handlePlayerAction(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                new BlockPos(0, 0, 0),
                Direction.DOWN
        ));
    }

}
