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
 * Modified by yigemingzii, September 2026 for Minecraft 26.3 and ProtocolLib support.
 */
package io.github.hello09x.fakeplayer.v26_3.network;

import io.github.hello09x.fakeplayer.core.network.FakeChannel;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;

public class FakeConnection extends Connection {

    private final FakeChannel fakeChannel;

    public FakeConnection(@NotNull InetAddress address) {
        super(PacketFlow.SERVERBOUND);
        this.fakeChannel = new FakeChannel(null, address);
        this.channel = this.fakeChannel;
        this.address = this.channel.remoteAddress();
        Connection.configureSerialization(this.channel.pipeline(), PacketFlow.SERVERBOUND, false, null);
    }

    public @NotNull FakeChannel getChannel() {
        return this.fakeChannel;
    }

    @Override
    public boolean isConnected() {
        return super.isConnected();
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener channelfuturelistener) {
        this.completeSend(channelfuturelistener);
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener channelfuturelistener, boolean flag) {
        this.completeSend(channelfuturelistener);
    }

    @Override
    public void send(Packet<?> packet) {
        this.completeSend(null);
    }

    private void completeSend(@Nullable ChannelFutureListener listener) {
        if (listener != null) {
            try {
                listener.operationComplete(this.channel.newSucceededFuture());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to complete fake connection send", e);
            }
        }

        if (!this.isConnected()) {
            this.handleDisconnection();
        }
    }

}
